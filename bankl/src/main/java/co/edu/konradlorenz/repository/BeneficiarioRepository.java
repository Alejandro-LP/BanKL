package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Integer> {
    List<Beneficiario> findByClienteIdDB(Integer clienteIdDB);
}