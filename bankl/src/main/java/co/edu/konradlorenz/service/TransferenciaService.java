package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransferenciaService {
    @Autowired private TransferenciaRepository transferenciaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private CuentaRepository cuentaRepository;
    @Autowired private NotificacionRepository notificacionRepository;

    public Transferencia realizar(String usuarioIS, String cuentaOrigenNum,
            String cuentaDestinoNum, String destinatario, double monto, String descripcion) {

        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) throw new RuntimeException("Cliente no encontrado");

        // Buscar cuenta origen y descontar saldo
        List<Cuenta> cuentas = cuentaRepository.findByClienteIdDB(cliente.getIdDB());
        Cuenta origen = cuentas.stream()
            .filter(c -> c.getNumeroTarjeta().replaceAll(" ", "").equals(cuentaOrigenNum.replaceAll(" ", "")))
            .findFirst().orElse(null);

        if (origen == null || !origen.retirar(monto)) {
            Transferencia fallida = Transferencia.crear(cliente, cuentaOrigenNum, cuentaDestinoNum, destinatario, monto, descripcion);
            fallida.setEstado("FALLIDA");
            return transferenciaRepository.save(fallida);
        }

        cuentaRepository.save(origen);
        Transferencia t = Transferencia.crear(cliente, cuentaOrigenNum, cuentaDestinoNum, destinatario, monto, descripcion);
        transferenciaRepository.save(t);

        notificacionRepository.save(Notificacion.crear(cliente,
            "Transferencia enviada",
            "Transferiste $" + monto + " a " + destinatario,
            "ALERTA"));
        return t;
    }

    public List<Transferencia> listar(String usuarioIS) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) return List.of();
        return transferenciaRepository.findByClienteIdDB(cliente.getIdDB());
    }
}