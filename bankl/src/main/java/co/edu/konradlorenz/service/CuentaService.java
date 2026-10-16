package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.repository.CuentaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

@Service
public class CuentaService {
    @Value("${bankl.avance.comision-porcentaje:0.03}")
    private BigDecimal comisionAvancePorcentaje;
    @Value("${bankl.avance.comision-minima:5000.00}")
    private BigDecimal comisionAvanceMinima;
    @Value("${bankl.avance.tasa-mensual:0.02}")
    private BigDecimal tasaAvanceMensual;

    @PostConstruct
    void validarParametrosAvance() {
        if (comisionAvancePorcentaje.signum() < 0 || comisionAvanceMinima.signum() < 0 ||
                tasaAvanceMensual.signum() < 0 || tasaAvanceMensual.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Configuración de tasas y comisiones de avance inválida");
        }
    }

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

    @Transactional(readOnly = true)
    public List<MovimientoView> listarMovimientos(String usuario) {
        Cliente cliente = ownershipGuard.requireCustomer(usuario);
        return cuentaRepository.findByClienteIdDB(cliente.getIdDB()).stream()
            .flatMap(cuenta -> cuenta.getHistorial().stream().map(movimiento -> new MovimientoView(
                movimiento.getId(), cuenta.getNumeroCuenta(), cuenta.getTipo(), movimiento.getTipo(),
                movimiento.getValor(), movimiento.getFecha())))
            .sorted(Comparator.comparing(MovimientoView::fecha).reversed())
            .limit(20).toList();
    }

    public record MovimientoView(Integer id, int numeroCuenta, String tipoCuenta, String tipo,
                                  double valor, java.time.LocalDateTime fecha) {}

    @Transactional
    public void consignar(String usuario, Integer idCuenta, double valor) {
        Cuenta cuenta = requireOwnedLockedAccount(usuario, idCuenta);
        requirePositiveAmount(valor);
        if (!"DEBITO".equals(cuenta.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La consignación requiere una cuenta débito");
        }
        requireBackedDebitAccount(cuenta);
        if (!cuenta.consignar(valor)) throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pudo acreditar la consignación simulada");
        cuenta.registrarMovimiento("ENTRADA:CONSIGNACION_SIMULADA", valor);
        cuentaRepository.save(cuenta);
    }

    /** Abona inmediatamente el efectivo y carga la comisión al cupo de la tarjeta. */
    public AvanceCostos cotizarAvance(double valor) {
        requirePositiveAmount(valor);
        BigDecimal monto = BigDecimal.valueOf(valor).setScale(2, RoundingMode.UNNECESSARY);
        BigDecimal comision = monto.multiply(comisionAvancePorcentaje).max(comisionAvanceMinima).setScale(2, RoundingMode.HALF_UP);
        BigDecimal interes = monto.multiply(tasaAvanceMensual).setScale(2, RoundingMode.HALF_UP);
        return new AvanceCostos(monto.doubleValue(), comision.doubleValue(), interes.doubleValue(), monto.add(comision).add(interes).doubleValue(),
            tasaAvanceMensual.multiply(BigDecimal.valueOf(100)).doubleValue(),
            comisionAvancePorcentaje.multiply(BigDecimal.valueOf(100)).doubleValue(), comisionAvanceMinima.doubleValue());
    }

    @Transactional
    public AvanceResult solicitarAvance(String usuario, Integer idCredito, Integer idDestino, double valor) {
        requirePositiveAmount(valor);
        if (idCredito == null || idDestino == null || idCredito.equals(idDestino))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona tarjetas de crédito y débito distintas");
        Cliente cliente = ownershipGuard.requireCustomer(usuario);
        Cuenta primera = lockedAccount(Math.min(idCredito, idDestino));
        Cuenta segunda = lockedAccount(Math.max(idCredito, idDestino));
        Cuenta credito = primera.getId().equals(idCredito) ? primera : segunda;
        Cuenta destino = primera.getId().equals(idDestino) ? primera : segunda;
        ownershipGuard.requireOwner(cliente, credito.getCliente());
        ownershipGuard.requireOwner(cliente, destino.getCliente());
        if (!"CREDITO".equals(credito.getTipo()) || !"DEBITO".equals(destino.getTipo()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El avance requiere una tarjeta de crédito y una cuenta débito");
        requireBackedDebitAccount(destino);
        AvanceCostos costos = cotizarAvance(valor);
        double monto = costos.monto();
        double comision = costos.comision();
        double interes = costos.interes30Dias();
        double cargoTotal = costos.cargoTotal();
        if (!credito.retirar(cargoTotal))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cupo disponible insuficiente para el monto y la comisión");
        if (!destino.consignar(monto))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pudo abonar la cuenta destino");
        credito.registrarMovimiento("AVANCE_PRINCIPAL", monto);
        credito.registrarMovimiento("COMISION_AVANCE", comision);
        credito.registrarMovimiento("INTERES_AVANCE_30_DIAS", interes);
        destino.registrarMovimiento("ENTRADA:AVANCE_TARJETA_CREDITO", monto);
        cuentaRepository.save(credito);
        cuentaRepository.save(destino);
        return new AvanceResult(monto, comision, interes, cargoTotal, destino.getSaldo());
    }

    public record AvanceCostos(double monto, double comision, double interes30Dias, double cargoTotal,
                                double tasaMensualPorcentaje, double comisionPorcentaje, double comisionMinima) {}
    public record AvanceResult(double monto, double comision, double interes30Dias, double cargoTotal, double saldoDestino) {}

    @Transactional
    public boolean retirar(String usuario, Integer idCuenta, double valor) {
        Cuenta cuenta = requireOwnedLockedAccount(usuario, idCuenta);
        requirePositiveAmount(valor);
        if (!"DEBITO".equals(cuenta.getTipo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Los retiros requieren una cuenta débito; usa la opción de avances para la tarjeta de crédito");
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
        if (!Double.isFinite(valor) || valor <= 0 || valor > 1000000000 || BigDecimal.valueOf(valor).scale() > 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor debe ser positivo, tener máximo dos decimales y no exceder $1.000.000.000");
        }
    }

    private void requireBackedDebitAccount(Cuenta cuenta) {
        if (!"DEBITO".equals(cuenta.getTipo()) || !cuenta.saldoDebitoRespaldado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El saldo no coincide con movimientos financiados y registrados");
        }
    }
}
