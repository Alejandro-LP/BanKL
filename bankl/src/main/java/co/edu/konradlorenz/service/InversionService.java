package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class InversionService {
    private final InversionRepository inversionRepository;
    private final NotificacionRepository notificacionRepository;
    private final OwnershipGuard ownershipGuard;
    private final CuentaService cuentaService;
    private final TransaccionRepository transaccionRepository;

    public InversionService(InversionRepository inversionRepository, NotificacionRepository notificacionRepository,
            OwnershipGuard ownershipGuard, CuentaService cuentaService, TransaccionRepository transaccionRepository) {
        this.inversionRepository = inversionRepository;
        this.notificacionRepository = notificacionRepository;
        this.ownershipGuard = ownershipGuard;
        this.cuentaService = cuentaService;
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional
    public Inversion crear(String usuarioIS, Integer idCuenta, double monto, double tasa, int plazo, String tipo) {
        validarProducto(monto, tasa, plazo, tipo);
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        Inversion inv = Inversion.crear(cliente, monto, tasa, plazo, tipo);
        inversionRepository.save(inv);
        cuentaService.retirarParaInversion(usuarioIS, idCuenta, monto, String.valueOf(inv.getId()));
        notificacionRepository.save(Notificacion.crear(cliente,
            "Inversión creada",
            "Tu " + tipo + " por $" + monto + " fue financiado desde tu cuenta. El rendimiento mostrado es una proyección y no se desembolsa sin fondos de respaldo.",
            "INFO"));
        return inv;
    }

    public List<Inversion> listar(String usuarioIS) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        return inversionRepository.findByClienteIdDB(cliente.getIdDB());
    }

    @Transactional
    public boolean retirar(String usuarioIS, Integer id, Integer idCuentaDestino) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        Inversion inv = inversionRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inversión no encontrada"));
        ownershipGuard.requireOwner(cliente, inv.getCliente());
        if (!"ACTIVA".equals(inv.getEstado())) return false;
        if (!transaccionRepository.existsByTipoAndCliente(
                "SALIDA:INVERSION_APERTURA_" + inv.getId(), cliente.getIdDB())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "La inversión no tiene un registro verificable del capital financiado");
        }
        if (LocalDate.now().isBefore(inv.getFechaVencimiento())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El capital solo se devuelve al vencimiento");
        }
        cuentaService.abonarRetiroInversion(usuarioIS, idCuentaDestino, inv.getMonto(), String.valueOf(inv.getId()));
        inv.setEstado("RETIRADA");
        inversionRepository.save(inv);
        return true;
    }

    private void validarProducto(double monto, double tasa, int plazo, String tipo) {
        if (!Double.isFinite(monto) || monto <= 0 || !Double.isFinite(tasa) || tasa < 0 ||
                plazo < 1 || plazo > 60 || !("CDT".equals(tipo) || "AHORRO_PROGRAMADO".equals(tipo))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de inversiÃ³n invÃ¡lidos");
        }
    }
}
