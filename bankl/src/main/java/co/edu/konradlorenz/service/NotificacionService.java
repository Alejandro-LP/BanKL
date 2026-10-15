package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class NotificacionService {
    private final NotificacionRepository notificacionRepository;
    private final OwnershipGuard ownershipGuard;

    public NotificacionService(NotificacionRepository notificacionRepository, OwnershipGuard ownershipGuard) {
        this.notificacionRepository = notificacionRepository;
        this.ownershipGuard = ownershipGuard;
    }

    public List<Notificacion> listar(String usuarioIS) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        return notificacionRepository.findByClienteIdDB(cliente.getIdDB());
    }

    public void marcarLeida(String usuarioIS, Integer id) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        Notificacion n = notificacionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificación no encontrada"));
        ownershipGuard.requireOwner(cliente, n.getCliente());
        n.setLeida(true);
        notificacionRepository.save(n);
    }
}
