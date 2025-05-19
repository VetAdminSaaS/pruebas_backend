package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.Entity.recuperarContrasenaToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface recuperarContrasenaTokenRepository extends JpaRepository<recuperarContrasenaToken, Long> {
    Optional<recuperarContrasenaToken> findByToken(String token);
    Optional<recuperarContrasenaToken> findByUsuario(Usuario usuario);
    void deleteByUsuario(Usuario usuario);

}
