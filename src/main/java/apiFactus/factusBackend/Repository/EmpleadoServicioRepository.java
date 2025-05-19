package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.EmpleadoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoServicioRepository extends JpaRepository<EmpleadoServicio, Long> {
    void deleteByEmpleadoVeterinarioId(Long empleadoVeterinarioId);
}
