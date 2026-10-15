package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final NotificacionRepository notificacionRepository;
    private final OwnershipGuard ownershipGuard;
    private final CuentaService cuentaService;
    private final TransaccionRepository transaccionRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, NotificacionRepository notificacionRepository,
            OwnershipGuard ownershipGuard, CuentaService cuentaService, TransaccionRepository transaccionRepository) {
        this.prestamoRepository = prestamoRepository;
        this.notificacionRepository = notificacionRepository;
        this.ownershipGuard = ownershipGuard;
        this.cuentaService = cuentaService;
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional
    public Prestamo solicitar(String usuarioIS, double monto, double tasa, int cuotas) {
        if (!Double.isFinite(monto) || monto < 100 || !Double.isFinite(tasa) || tasa < 0 || tasa > 100 ||
                cuotas < 1 || cuotas > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de préstamo inválidos");
        }
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        Prestamo p = Prestamo.crear(cliente, monto, tasa, cuotas);
        prestamoRepository.save(p);
        notificacionRepository.save(Notificacion.crear(cliente,
            "Solicitud de préstamo recibida",
            "Tu solicitud por $" + monto + " está pendiente de aprobación y desembolso. No se han movido fondos.",
            "INFO"));
        return p;
    }

    public List<Prestamo> listar(String usuarioIS) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        return prestamoRepository.findByClienteIdDB(cliente.getIdDB());
    }

    @Transactional
    public boolean pagarCuota(String usuarioIS, Integer prestamoId, Integer idCuenta) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        Prestamo p = prestamoRepository.findByIdForUpdate(prestamoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));
        ownershipGuard.requireOwner(cliente, p.getCliente());
        if (!"ACTIVO".equals(p.getEstado())) return false;
        if (!transaccionRepository.existsByTipo("ENTRADA:PRESTAMO_DESEMBOLSO_" + p.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El préstamo no tiene un desembolso registrado que habilite el cobro de cuotas");
        }
        cuentaService.pagarCuotaPrestamo(usuarioIS, idCuenta, p.getValorCuota(), p.getId());
        boolean ok = p.pagarCuota();
        prestamoRepository.save(p);
        if (p.getEstado().equals("PAGADO")) {
            notificacionRepository.save(Notificacion.crear(p.getCliente(),
                "Préstamo pagado", "¡Felicitaciones! Tu préstamo ha sido pagado.", "INFO"));
        }
        return ok;
    }
}
