package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PrestamoService {
    @Autowired private PrestamoRepository prestamoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private NotificacionRepository notificacionRepository;

    public Prestamo solicitar(String usuarioIS, double monto, double tasa, int cuotas) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) throw new RuntimeException("Cliente no encontrado");
        Prestamo p = Prestamo.crear(cliente, monto, tasa, cuotas);
        prestamoRepository.save(p);
        notificacionRepository.save(Notificacion.crear(cliente,
            "Préstamo aprobado",
            "Tu préstamo de $" + monto + " fue aprobado. Cuota mensual: $" + p.getValorCuota(),
            "INFO"));
        return p;
    }

    public List<Prestamo> listar(String usuarioIS) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) return List.of();
        return prestamoRepository.findByClienteIdDB(cliente.getIdDB());
    }

    public boolean pagarCuota(Integer prestamoId) {
        Prestamo p = prestamoRepository.findById(prestamoId).orElse(null);
        if (p == null) return false;
        boolean ok = p.pagarCuota();
        prestamoRepository.save(p);
        if (p.getEstado().equals("PAGADO")) {
            notificacionRepository.save(Notificacion.crear(p.getCliente(),
                "Préstamo pagado", "¡Felicitaciones! Tu préstamo ha sido pagado.", "INFO"));
        }
        return ok;
    }
}