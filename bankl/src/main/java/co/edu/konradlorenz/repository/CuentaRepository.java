package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    List<Cuenta> findByClienteIdDB(Integer clienteIdDB);
}