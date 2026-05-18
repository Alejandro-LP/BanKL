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

    public Transferencia realizar(String usuarioIS, Integer cuentaOrigenId,
            String cuentaDestinoNum, String destinatario, double monto, String descripcion) {

        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) throw new RuntimeException("Cliente no encontrado");

        // Buscar cuenta origen por ID
        Cuenta origen = cuentaRepository.findById(cuentaOrigenId).orElse(null);

        if (origen == null || !origen.retirar(monto)) {
            Transferencia fallida = Transferencia.crear(cliente,
                origen != null ? String.valueOf(origen.getNumeroCuenta()) : "DESCONOCIDA",
                cuentaDestinoNum, destinatario, monto, descripcion);
            fallida.setEstado("FALLIDA");
            return transferenciaRepository.save(fallida);
        }

        // Buscar cuenta destino por numeroCuenta y acreditarle el dinero
        Cuenta destino = cuentaRepository.findByNumeroCuenta(Integer.parseInt(cuentaDestinoNum.trim()));
        if (destino != null) {
            destino.consignar(monto);
            cuentaRepository.save(destino);

            // Notificar al destinatario
            if (destino.getCliente() != null) {
                notificacionRepository.save(Notificacion.crear(destino.getCliente(),
                    "Transferencia recibida",
                    "Recibiste $" + monto + " de " + cliente.getNombres() + " " + cliente.getApellidos(),
                    "INFO"));
            }
        }

        cuentaRepository.save(origen);

        Transferencia t = Transferencia.crear(cliente,
            String.valueOf(origen.getNumeroCuenta()),
            cuentaDestinoNum, destinatario, monto, descripcion);
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