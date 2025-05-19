package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Invitado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface InvitadoRepository extends JpaRepository<Invitado, Long> {
    List<Invitado> findByMesaNumero(int numero);
    Optional<Invitado> findByNombre(String nombre);
}
