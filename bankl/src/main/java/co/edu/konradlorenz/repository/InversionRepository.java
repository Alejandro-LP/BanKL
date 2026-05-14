package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Inversion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InversionRepository extends JpaRepository<Inversion, Integer> {
    List<Inversion> findByClienteIdDB(Integer clienteIdDB);
}