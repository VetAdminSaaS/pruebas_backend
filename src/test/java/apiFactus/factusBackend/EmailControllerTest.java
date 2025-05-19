package apiFactus.factusBackend;

import apiFactus.factusBackend.Dto.MascotaRequestDTO;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmailService emailService;

    @Test
    void testEmailEndpoint() throws Exception {
        MascotaRequestDTO mascotaRequestDTO = new MascotaRequestDTO();
        mascotaRequestDTO.setNombreCompleto("Hachiko");
        mascotaRequestDTO.setDescripcion("MASCOTA PRUEBA");

    }
}
