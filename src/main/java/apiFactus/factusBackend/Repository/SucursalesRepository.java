package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Sucursales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SucursalesRepository extends JpaRepository<Sucursales, Long> {
    Optional<Sucursales> findByNombre (String nombre);
}
