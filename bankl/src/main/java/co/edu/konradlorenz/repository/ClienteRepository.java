package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Cliente findByUsuarioIS(String usuarioIS);
}