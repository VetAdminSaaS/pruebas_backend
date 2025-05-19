package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.DireccionEnvio;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DireccionEnvioRepository extends JpaRepository<DireccionEnvio, Long> {
    List<DireccionEnvio> findByUsuario(Usuario usuario);
    Optional<DireccionEnvio> findByDireccion(String direccion);
    boolean existsByDireccionAndUsuario(String direccion, Usuario usuario);
    boolean existsByDireccionAndUsuarioAndIdNot(String direccion, Usuario usuario, Long id);



}
