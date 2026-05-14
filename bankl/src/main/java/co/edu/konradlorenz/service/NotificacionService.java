package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificacionService {
    @Autowired private NotificacionRepository notificacionRepository;
    @Autowired private ClienteRepository clienteRepository;

    public List<Notificacion> listar(String usuarioIS) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) return List.of();
        return notificacionRepository.findByClienteIdDB(cliente.getIdDB());
    }

    public void marcarLeida(Integer id) {
        Notificacion n = notificacionRepository.findById(id).orElse(null);
        if (n != null) { n.setLeida(true); notificacionRepository.save(n); }
    }
}