package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InversionService {
    @Autowired private InversionRepository inversionRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private NotificacionRepository notificacionRepository;

    public Inversion crear(String usuarioIS, double monto, double tasa, int plazo, String tipo) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) throw new RuntimeException("Cliente no encontrado");
        Inversion inv = Inversion.crear(cliente, monto, tasa, plazo, tipo);
        inversionRepository.save(inv);
        notificacionRepository.save(Notificacion.crear(cliente,
            "Inversión creada",
            "Tu " + tipo + " de $" + monto + " fue creado. Rendimiento: $" + inv.getRendimientoTotal(),
            "INFO"));
        return inv;
    }

    public List<Inversion> listar(String usuarioIS) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) return List.of();
        return inversionRepository.findByClienteIdDB(cliente.getIdDB());
    }

    public boolean retirar(Integer id) {
        Inversion inv = inversionRepository.findById(id).orElse(null);
        if (inv == null || !inv.getEstado().equals("ACTIVA")) return false;
        inv.setEstado("RETIRADA");
        inversionRepository.save(inv);
        return true;
    }
}