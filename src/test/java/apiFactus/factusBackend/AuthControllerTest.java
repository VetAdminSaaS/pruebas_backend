package apiFactus.factusBackend;

import apiFactus.factusBackend.Dto.AuthResponse;
import apiFactus.factusBackend.Dto.LoginDTO;
import apiFactus.factusBackend.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void testLoginEndpoint() throws Exception {
        // Arrange
        AuthResponse authResponse = new AuthResponse();
        authResponse.setNames("test@email.com");
        authResponse.setToken("mocked-jwt-token");

        when(usuarioService.login(any())).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "test@email.com",
                                "password": "1234"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").value("test@email.com"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }
    void testLoginGoogleEndpoint() throws  Exception {
        AuthResponse authResponse = new AuthResponse();

    }
}
