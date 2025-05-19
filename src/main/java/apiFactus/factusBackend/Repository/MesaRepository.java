package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<mesa, Long> {

}
