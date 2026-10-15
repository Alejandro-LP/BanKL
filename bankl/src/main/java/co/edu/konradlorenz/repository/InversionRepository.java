package co.edu.konradlorenz.repository;

import co.edu.konradlorenz.model.Inversion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InversionRepository extends JpaRepository<Inversion, Integer> {
    List<Inversion> findByClienteIdDB(Integer clienteIdDB);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inversion i where i.id = :id")
    Optional<Inversion> findByIdForUpdate(@Param("id") Integer id);
}
