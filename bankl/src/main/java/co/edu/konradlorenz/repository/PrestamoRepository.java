package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {
    List<Prestamo> findByClienteIdDB(Integer clienteIdDB);
}