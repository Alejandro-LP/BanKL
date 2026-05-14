package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
    List<Transferencia> findByClienteIdDB(Integer clienteIdDB);
}