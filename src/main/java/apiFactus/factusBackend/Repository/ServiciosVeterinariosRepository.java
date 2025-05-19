package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.ServiciosVeterinarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiciosVeterinariosRepository extends JpaRepository<ServiciosVeterinarios, Long>{
    Optional<ServiciosVeterinarios> findByNombre(String nombre);
    List<ServiciosVeterinarios> findByNombreIn(List<String> nombres);

}
