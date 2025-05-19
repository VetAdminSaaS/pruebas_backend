package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.reactivationToken;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactivationTokenRepository extends JpaRepository<reactivationToken, Long> {
    Optional<reactivationToken> findByToken(String token);
    Optional<reactivationToken> findByUsuario(Usuario usuario);
    void deleteByUsuario(Usuario usuario);
}
