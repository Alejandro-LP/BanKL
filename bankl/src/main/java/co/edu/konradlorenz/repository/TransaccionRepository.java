package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Transaccion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    boolean existsByTipo(String tipo);

    @Query("select case when count(t) > 0 then true else false end " +
           "from Transaccion t where t.tipo = :tipo and t.cuenta.cliente.idDB = :clienteId")
    boolean existsByTipoAndCliente(@Param("tipo") String tipo, @Param("clienteId") Integer clienteId);
}
