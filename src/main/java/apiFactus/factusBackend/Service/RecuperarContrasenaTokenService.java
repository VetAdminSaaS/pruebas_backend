package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Domain.Entity.recuperarContrasenaToken;
import jakarta.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;

public interface RecuperarContrasenaTokenService {

    void createAndSendPasswordResetToken(String email) throws Exception;

    recuperarContrasenaToken findByToken(String token);

    void eliminarRecuperacionToken(recuperarContrasenaToken recuperarContrasenaToken);

    boolean isValidToken(String token);

    void recuperarContrasena(String token, String newContrasena);
}
