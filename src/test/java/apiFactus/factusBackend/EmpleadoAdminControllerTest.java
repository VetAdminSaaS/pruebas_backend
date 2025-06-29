package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.EmpleadoAdminController;
import apiFactus.factusBackend.Dto.*;
import apiFactus.factusBackend.Service.EmpleadoService;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoAdminControllerTest {

    @Mock
    private EmpleadoService empleadoService;

    @InjectMocks
    private EmpleadoAdminController empleadoAdminController;

    private EmpleadoRegistrationDTO empleadoRegistrationDTO;
    private EmpleadosDetailsDTO empleadosDetailsDTO;
    private EmpleadoProfileDTO empleadoProfileDTO;
    private EmpleadosDTO empleadosDTO;

    @BeforeEach
    void setUp() {
        // Configuración común para los DTOs
        empleadoRegistrationDTO = new EmpleadoRegistrationDTO();
        empleadoRegistrationDTO.setEmail("test@example.com");
        empleadoRegistrationDTO.setPassword("password123");
        empleadoRegistrationDTO.setNombre("Test");
        empleadoRegistrationDTO.setApellido("User");

        empleadosDetailsDTO = new EmpleadosDetailsDTO();
        empleadosDetailsDTO.setId(1L);
        empleadosDetailsDTO.setNombre("Test");
        empleadosDetailsDTO.setApellido("User");
        empleadosDetailsDTO.setEmail("test@example.com");

        empleadoProfileDTO = new EmpleadoProfileDTO();
        empleadoProfileDTO.setId(1L);
        empleadoProfileDTO.setNombre("Test");
        empleadoProfileDTO.setApellido("User");
        empleadoProfileDTO.setEmail("test@example.com");

        empleadosDTO = new EmpleadosDTO();
        empleadosDTO.setId(1L);
        empleadosDTO.setNombre("Test");
        empleadosDTO.setApellido("User");
        empleadosDTO.setEmail("test@example.com");
    }

    // Prueba para empleadosListALL()
    @Test
    void empleadosListALL_ShouldReturnListOfEmpleadosDetailsDTO() {
        // Arrange
        List<EmpleadosDetailsDTO> expectedList = Arrays.asList(empleadosDetailsDTO);
        when(empleadoService.getAll()).thenReturn(expectedList);

        // Act
        ResponseEntity<List<EmpleadosDetailsDTO>> response = empleadoAdminController.empleadosListALL();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test", response.getBody().get(0).getNombre());
        verify(empleadoService, times(1)).getAll();
    }

    // Prueba para create()
    @Test
    void create_ShouldReturnCreatedEmpleado() throws MessagingException, BadRequestException {
        // Arrange
        when(empleadoService.crearEmpleado(empleadoRegistrationDTO)).thenReturn(empleadoRegistrationDTO);

        // Act
        ResponseEntity<EmpleadoRegistrationDTO> response =
                empleadoAdminController.create(empleadoRegistrationDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(empleadoService, times(1)).crearEmpleado(empleadoRegistrationDTO);
    }

    // Prueba para empleadosDetailsByID()
    @Test
    void empleadosDetailsByID_ShouldReturnEmpleadoDetails() {
        // Arrange
        Long id = 1L;
        when(empleadoService.findById(id)).thenReturn(empleadosDetailsDTO);

        // Act
        ResponseEntity<EmpleadosDetailsDTO> response = empleadoAdminController.empleadosDetailsByID(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        verify(empleadoService, times(1)).findById(id);
    }

    // Prueba para update()
    @Test
    void update_ShouldReturnUpdatedEmpleado() {
        // Arrange
        Long id = 1L;
        when(empleadoService.update(id, empleadosDetailsDTO)).thenReturn(empleadosDetailsDTO);

        // Act
        ResponseEntity<EmpleadosDetailsDTO> response =
                empleadoAdminController.update(id, empleadosDetailsDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        verify(empleadoService, times(1)).update(id, empleadosDetailsDTO);
    }

    // Prueba para delete()
    @Test
    void delete_ShouldCallServiceAndReturnNoContent() {
        // Arrange
        Long id = 1L;
        doNothing().when(empleadoService).delete(id);

        // Act
        ResponseEntity<Void> response = empleadoAdminController.delete(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(empleadoService, times(1)).delete(id);
    }

    // Prueba para register()
    @Test
    void register_ShouldReturnCreatedEmpleadoProfile() throws MessagingException, BadRequestException {
        // Arrange
        when(empleadoService.registroEmpleadoVeterinario(empleadoRegistrationDTO))
                .thenReturn(empleadoProfileDTO);

        // Act
        ResponseEntity<EmpleadoProfileDTO> response =
                empleadoAdminController.register(empleadoRegistrationDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(empleadoService, times(1)).registroEmpleadoVeterinario(empleadoRegistrationDTO);
    }

    // Prueba para completarRegistro()
    @Test
    void completarRegistro_ShouldReturnUpdatedEmpleado() {
        // Arrange
        Long id = 1L;
        when(empleadoService.completarRegistro(id, empleadosDTO)).thenReturn(empleadosDTO);

        // Act
        ResponseEntity<EmpleadosDTO> response =
                empleadoAdminController.completarRegistro(id, empleadosDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody().getId());
        verify(empleadoService, times(1)).completarRegistro(id, empleadosDTO);
    }

    // Prueba para manejo de excepciones en create()
    @Test
    void create_WhenServiceThrowsException_ShouldPropagateException() throws MessagingException, BadRequestException {
        // Arrange
        when(empleadoService.crearEmpleado(empleadoRegistrationDTO))
                .thenThrow(new BadRequestException("Error en el servicio"));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            empleadoAdminController.create(empleadoRegistrationDTO);
        });
    }


    @Test
    void register_WhenServiceThrowsMessagingException_ShouldPropagateException() throws MessagingException, BadRequestException {
        // Arrange
        when(empleadoService.registroEmpleadoVeterinario(empleadoRegistrationDTO))
                .thenThrow(new MessagingException("Error de email"));

        // Act & Assert
        assertThrows(MessagingException.class, () -> {
            empleadoAdminController.register(empleadoRegistrationDTO);
        });
    }
}