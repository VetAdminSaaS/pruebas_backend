package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.EspecialidadController;
import apiFactus.factusBackend.Dto.EspecialidadDTO;
import apiFactus.factusBackend.Service.EspecialidadService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecialidadControllerTest {

    @Mock
    private EspecialidadService especialidadService;

    @InjectMocks
    private EspecialidadController controller;

    private EspecialidadDTO especialidadDTO;

    @BeforeEach
    void setUp() {
        especialidadDTO = new EspecialidadDTO();
        especialidadDTO.setId(1L);
        especialidadDTO.setNombre("Dermatología");
    }

    @Test
    void getAllEspecialidades_ShouldReturnList() {
        // Arrange
        List<EspecialidadDTO> especialidades = List.of(especialidadDTO);
        when(especialidadService.getAllEspecialidad()).thenReturn(especialidades);

        // Act
        ResponseEntity<List<EspecialidadDTO>> response = controller.getAllEspecialidades();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(especialidadService, times(1)).getAllEspecialidad();
    }

    @Test
    void getAllEspecialidadesPageable_ShouldReturnPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre"));
        Page<EspecialidadDTO> especialidadPage = new PageImpl<>(List.of(especialidadDTO));
        when(especialidadService.getAllEspecialidadDTO(pageable)).thenReturn(especialidadPage);

        // Act
        ResponseEntity<Page<EspecialidadDTO>> response = controller.getAllEspecialidades(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        verify(especialidadService, times(1)).getAllEspecialidadDTO(pageable);
    }

    @Test
    void getEspecialidadeById_ShouldReturnEspecialidad() {
        // Arrange
        when(especialidadService.findById(1L)).thenReturn(especialidadDTO);

        // Act
        ResponseEntity<EspecialidadDTO> response = controller.getEspecialidadeById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dermatología", response.getBody().getNombre());
        verify(especialidadService, times(1)).findById(1L);
    }

    @Test
    void createEspecialidade_ShouldReturnCreated() {
        // Arrange
        when(especialidadService.createEspecialidad(especialidadDTO)).thenReturn(especialidadDTO);

        // Act
        ResponseEntity<EspecialidadDTO> response = controller.createEspecialidade(especialidadDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Dermatología", response.getBody().getNombre());
        verify(especialidadService, times(1)).createEspecialidad(especialidadDTO);
    }

    @Test
    void updateEspecialidade_ShouldReturnUpdated() {
        // Arrange
        when(especialidadService.updateEspecialidad(1L, especialidadDTO)).thenReturn(especialidadDTO);

        // Act
        ResponseEntity<EspecialidadDTO> response = controller.updateEspecialidade(especialidadDTO, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dermatología", response.getBody().getNombre());
        verify(especialidadService, times(1)).updateEspecialidad(1L, especialidadDTO);
    }

    @Test
    void deleteEspecialidade_ShouldReturnOk() throws BadRequestException {
        // Arrange
        doNothing().when(especialidadService).deleteEspecialidad(1L);

        // Act
        ResponseEntity<Void> response = controller.deleteEspecialidade(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(especialidadService, times(1)).deleteEspecialidad(1L);
    }
}
