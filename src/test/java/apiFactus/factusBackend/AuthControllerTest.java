import apiFactus.factusBackend.Controller.AuthController;
import apiFactus.factusBackend.Dto.AuthResponse;
import apiFactus.factusBackend.Dto.LoginDTO;
import apiFactus.factusBackend.Service.TokenReactivationService;
import apiFactus.factusBackend.Service.UsuarioService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UsuarioService usuarioService;
    private TokenReactivationService tokenReactivationService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        tokenReactivationService = mock(TokenReactivationService.class);
        authController = new AuthController(usuarioService, tokenReactivationService);
    }

    @Test
    void testLogin_returnAuthResponse() throws MessagingException {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("cliente@vet.com");
        loginDTO.setPassword("123456");

        AuthResponse expected = new AuthResponse();
        expected.setToken("abc123token");
        expected.setRole("ROLE_CLIENT");
        expected.setNames("María Ruiz");

        when(usuarioService.login(loginDTO)).thenReturn(expected);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("abc123token", response.getBody().getToken());
        assertEquals("ROLE_CLIENT", response.getBody().getRole());
        assertEquals("María Ruiz", response.getBody().getNames());
    }
}
