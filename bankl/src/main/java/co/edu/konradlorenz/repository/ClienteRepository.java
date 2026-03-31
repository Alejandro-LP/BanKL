package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.ClienteNatural;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteNatural, Integer> {

    ClienteNatural findByUsuarioIS(String usuarioIS);
}