package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByClienteIdDB(Integer clienteIdDB);
}