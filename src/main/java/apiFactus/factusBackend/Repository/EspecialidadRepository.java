package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    List<Especialidad> findByNombreIn(List<String> nombres);
    Optional<Especialidad> findByNombre(String nombre);

}
