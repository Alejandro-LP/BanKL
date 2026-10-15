package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.repository.CuentaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final OwnershipGuard ownershipGuard;

    public CuentaService(CuentaRepository cuentaRepository, OwnershipGuard ownershipGuard) {
        this.cuentaRepository = cuentaRepository;
        this.ownershipGuard = ownershipGuard;
    }

    public Cuenta guardarCuenta(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public Cuenta buscarCuenta(Integer id) {
        return cuentaRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean consignar(String usuario, Integer idCuenta, double valor) {
        requireOwnedLockedAccount(usuario, idCuenta);
        throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Consignación directa deshabilitada: no hay una fuente de fondos verificable");
    }

    @Transactional
    public boolean retirar(String usuario, Integer idCuenta, double valor) {
        Cuenta cuenta = requireOwnedLockedAccount(usuario, idCuenta);
        requirePositiveAmount(valor);
        if (!"DEBITO".equals(cuenta.getTipo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Los avances de crédito están deshabilitados hasta integrar una fuente bancaria verificable");
        }
        requireBackedDebitAccount(cuenta);
        if (!cuenta.retirar(valor)) return false;
        cuenta.registrarMovimiento("SALIDA:RETIRO", valor);
        cuentaRepository.save(cuenta);
        return true;
    }

    @Transactional
    public void pagarTarjeta(String usuario, Integer idCuentaCredito, Integer idCuentaOrigen, double valor) {
        requirePositiveAmount(valor);
        if (idCuentaCredito.equals(idCuentaOrigen)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona una cuenta de origen distinta");
        }

        Cliente cliente = ownershipGuard.requireCustomer(usuario);
        Cuenta primera = lockedAccount(Math.min(idCuentaCredito, idCuentaOrigen));
        Cuenta segunda = lockedAccount(Math.max(idCuentaCredito, idCuentaOrigen));
        Cuenta credito = primera.getId().equals(idCuentaCredito) ? primera : segunda;
        Cuenta origen = primera.getId().equals(idCuentaOrigen) ? primera : segunda;
        ownershipGuard.requireOwner(cliente, credito.getCliente());
        ownershipGuard.requireOwner(cliente, origen.getCliente());

        if (!"CREDITO".equals(credito.getTipo()) || !"DEBITO".equals(origen.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "El pago requiere una tarjeta de crédito y una cuenta débito propia");
        }
        requireBackedDebitAccount(origen);
        if (!origen.retirar(valor)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo débito insuficiente");
        }
        if (!credito.consignar(valor)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El pago excede la deuda de la tarjeta");
        }
        origen.registrarMovimiento("SALIDA:PAGO_TARJETA", valor);
        credito.registrarMovimiento("ABONO_TARJETA", valor);
    }

    @Transactional
    public void retirarParaInversion(String usuario, Integer idCuenta, double valor, String referencia) {
        requirePositiveAmount(valor);
        Cuenta cuenta = requireOwnedLockedAccount(usuario, idCuenta);
        if (!"DEBITO".equals(cuenta.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Las inversiones solo pueden financiarse desde una cuenta débito");
        }
        requireBackedDebitAccount(cuenta);
        if (!cuenta.retirar(valor)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo insuficiente para la inversión");
        }
        cuenta.registrarMovimiento("SALIDA:INVERSION_APERTURA_" + referencia, valor);
    }

    @Transactional
    public void abonarRetiroInversion(String usuario, Integer idCuenta, double valor, String referencia) {
        requirePositiveAmount(valor);
        Cuenta cuenta = requireOwnedLockedAccount(usuario, idCuenta);
        if (!"DEBITO".equals(cuenta.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "El capital solo puede abonarse a una cuenta débito");
        }
        requireBackedDebitAccount(cuenta);
        if (!cuenta.consignar(valor)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pudo devolver el capital invertido");
        }
        cuenta.registrarMovimiento("ENTRADA:INVERSION_RETIRO_" + referencia, valor);
    }

    @Transactional
    public void pagarCuotaPrestamo(String usuario, Integer idCuenta, double valor, Integer prestamoId) {
        requirePositiveAmount(valor);
        Cuenta cuenta = requireOwnedLockedAccount(usuario, idCuenta);
        if (!"DEBITO".equals(cuenta.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Las cuotas deben pagarse desde una cuenta débito");
        }
        requireBackedDebitAccount(cuenta);
        if (!cuenta.retirar(valor)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo insuficiente para pagar la cuota");
        }
        cuenta.registrarMovimiento("SALIDA:PAGO_CUOTA_PRESTAMO_" + prestamoId, valor);
    }

    @Transactional
    public boolean regenerarTarjeta(String usuario, Integer idCuenta) {
        Cuenta cuenta = requireOwnedLockedAccount(usuario, idCuenta);
        cuenta.regenerarTarjeta();
        cuentaRepository.save(cuenta);
        return true;
    }

    private Cuenta requireOwnedLockedAccount(String usuario, Integer idCuenta) {
        Cliente cliente = ownershipGuard.requireCustomer(usuario);
        Cuenta cuenta = lockedAccount(idCuenta);
        ownershipGuard.requireOwner(cliente, cuenta.getCliente());
        return cuenta;
    }

    private Cuenta lockedAccount(Integer idCuenta) {
        return cuentaRepository.findByIdForUpdate(idCuenta)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
    }

    private void requirePositiveAmount(double valor) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor debe ser mayor que cero");
        }
    }

    private void requireBackedDebitAccount(Cuenta cuenta) {
        if (!"DEBITO".equals(cuenta.getTipo()) || !cuenta.saldoDebitoRespaldado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El saldo no coincide con movimientos financiados y registrados");
        }
    }
}
