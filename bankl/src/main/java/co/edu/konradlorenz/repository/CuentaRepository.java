package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
}