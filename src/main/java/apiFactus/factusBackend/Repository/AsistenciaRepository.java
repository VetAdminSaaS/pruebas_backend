package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
}
