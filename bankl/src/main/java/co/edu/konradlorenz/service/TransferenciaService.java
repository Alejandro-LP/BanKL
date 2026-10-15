package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferenciaService {
    private final TransferenciaRepository transferenciaRepository;
    private final CuentaRepository cuentaRepository;
    private final NotificacionRepository notificacionRepository;
    private final OwnershipGuard ownershipGuard;

    public TransferenciaService(TransferenciaRepository transferenciaRepository, CuentaRepository cuentaRepository,
            NotificacionRepository notificacionRepository, OwnershipGuard ownershipGuard) {
        this.transferenciaRepository = transferenciaRepository;
        this.cuentaRepository = cuentaRepository;
        this.notificacionRepository = notificacionRepository;
        this.ownershipGuard = ownershipGuard;
    }

    @Transactional
    public Transferencia realizar(String usuarioIS, Integer cuentaOrigenId,
            String cuentaDestinoNum, String destinatario, double monto, String descripcion) {

        if (!Double.isFinite(monto) || monto <= 0 || cuentaDestinoNum == null || destinatario == null ||
                cuentaDestinoNum.length() > 20 || destinatario.length() > 120 ||
                (descripcion != null && descripcion.length() > 250))
            throw new IllegalArgumentException("Datos de transferencia inválidos");

        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);

        // Buscar cuenta origen por ID
        Cuenta origen = cuentaRepository.findByIdForUpdate(cuentaOrigenId).orElse(null);

        if (origen == null || origen.getCliente() == null || !origen.getCliente().getIdDB().equals(cliente.getIdDB()))
            throw new IllegalArgumentException("Cuenta origen inválida");

        // Buscar cuenta destino por numeroCuenta y acreditarle el dinero
        int numeroDestino;
        try { numeroDestino=Integer.parseInt(cuentaDestinoNum.trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Cuenta destino inválida"); }
        Cuenta destino = cuentaRepository.findByNumeroCuentaForUpdate(numeroDestino);
        if (!"DEBITO".equals(origen.getTipo()) || !origen.saldoDebitoRespaldado())
            throw new IllegalArgumentException("La cuenta origen no tiene saldo débito respaldado por el libro de movimientos");
        if (destino != null && "DEBITO".equals(destino.getTipo()) && !destino.saldoDebitoRespaldado())
            throw new IllegalArgumentException("La cuenta destino tiene saldo sin respaldo en el libro de movimientos");
        if (destino == null || destino.getId().equals(origen.getId()))
            throw new IllegalArgumentException("Cuenta destino inválida");
        if (!origen.retirar(monto)) throw new IllegalArgumentException("Fondos insuficientes");
        if (!destino.consignar(monto)) throw new IllegalArgumentException("No se pudo acreditar la cuenta destino");
        origen.registrarMovimiento("SALIDA:TRANSFERENCIA_ENVIADA", monto);
        destino.registrarMovimiento("ENTRADA:TRANSFERENCIA_RECIBIDA", monto);
        cuentaRepository.save(destino);

            // Notificar al destinatario
            if (destino.getCliente() != null) {
                notificacionRepository.save(Notificacion.crear(destino.getCliente(),
                    "Transferencia recibida",
                    "Recibiste $" + monto + " de " + cliente.getNombres() + " " + cliente.getApellidos(),
                    "INFO"));
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
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        return transferenciaRepository.findByClienteIdDB(cliente.getIdDB());
    }
}
