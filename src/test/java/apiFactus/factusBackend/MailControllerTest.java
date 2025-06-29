package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.MailController;
import apiFactus.factusBackend.Service.RecuperarContrasenaTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailControllerTest {

    @Mock
    private RecuperarContrasenaTokenService recuperarContrasenaTokenService;

    @InjectMocks
    private MailController mailController;

    private final String email = "usuario@correo.com";
    private final String token = "test-token";

    @BeforeEach
    void setUp() {

    }

    @Test
    void enviarRecuperarContrasenaEmail_ShouldReturnOk() throws Exception {
        // Arrange
        Map<String, String> request = Map.of("email", email);
        doNothing().when(recuperarContrasenaTokenService).createAndSendPasswordResetToken(email);

        // Act
        ResponseEntity<Void> response = mailController.enviarRecuperarContrasenaEmail(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recuperarContrasenaTokenService, times(1)).createAndSendPasswordResetToken(email);
    }

    @Test
    void verificarTokenValido_ShouldReturnTrue() {
        // Arrange
        when(recuperarContrasenaTokenService.isValidToken(token)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = mailController.verificarTokenValido(token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
        verify(recuperarContrasenaTokenService, times(1)).isValidToken(token);
    }

    @Test
    void verificarTokenValido_ShouldReturnFalse() {
        // Arrange
        when(recuperarContrasenaTokenService.isValidToken(token)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = mailController.verificarTokenValido(token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotEquals(Boolean.TRUE, response.getBody());
        verify(recuperarContrasenaTokenService, times(1)).isValidToken(token);
    }

    @Test
    void recuperarContrasena_ShouldReturnOk() {
        // Arrange
        String nuevaContrasena = "NuevaClave123!";
        Map<String, String> request = Map.of("newContrasena", nuevaContrasena);
        doNothing().when(recuperarContrasenaTokenService).recuperarContrasena(token, nuevaContrasena);

        // Act
        ResponseEntity<Void> response = mailController.recuperarContrasena(token, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recuperarContrasenaTokenService, times(1)).recuperarContrasena(token, nuevaContrasena);
    }
}
