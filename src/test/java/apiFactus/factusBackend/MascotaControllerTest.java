package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.MascotaController;
import apiFactus.factusBackend.Dto.MascotaRequestDTO;
import apiFactus.factusBackend.Dto.MascotaResponseDTO;
import apiFactus.factusBackend.Service.MascotaService;
import apiFactus.factusBackend.Domain.enums.Especie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaControllerTest {

    @Mock
    private MascotaService mascotaService;

    @InjectMocks
    private MascotaController mascotaController;

    private MascotaRequestDTO requestDTO;
    private MascotaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new MascotaRequestDTO();
        requestDTO.setNombreCompleto("Rocky");
        requestDTO.setRaza("Labrador");
        requestDTO.setEspecie(Especie.PERRO);
        requestDTO.setFechaNacimiento(LocalDate.of(2020, 1, 1));
        requestDTO.setPeso(25.0);
        requestDTO.setDescripcion("Muy juguetón");
        requestDTO.setEsterilizado(true);
        requestDTO.setApoderadoIds(List.of(1L, 2L));

        responseDTO = new MascotaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombreCompleto("Rocky");
        responseDTO.setRaza("Labrador");
    }

    @Test
    void crearMascota_ShouldReturnCreated() {
        when(mascotaService.crearMascota(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<MascotaResponseDTO> response = mascotaController.crearMascota(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Rocky", response.getBody().getNombreCompleto());
    }

    @Test
    void consultarMascota_ShouldReturnMascota() {
        when(mascotaService.obtenerMascotaPorId(1L)).thenReturn(responseDTO);

        ResponseEntity<MascotaResponseDTO> response = mascotaController.consultarMascota(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void listarMascotas_ShouldReturnList() {
        when(mascotaService.listarMascotas()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<MascotaResponseDTO>> response = mascotaController.listarMascotas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("Rocky", response.getBody().get(0).getNombreCompleto());
    }

    @Test
    void listarMascotasByApoderado_ShouldReturnList() {
        when(mascotaService.listarMascotasByApoderado()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<MascotaResponseDTO>> response = mascotaController.listarMascotasByApoderado();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rocky", response.getBody().get(0).getNombreCompleto());
    }

    @Test
    void actualizarMascota_ShouldReturnUpdatedMascota() {
        when(mascotaService.actualizarMascota(1L, requestDTO)).thenReturn(responseDTO);

        ResponseEntity<MascotaResponseDTO> response = mascotaController.actualizarMascota(1L, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rocky", response.getBody().getNombreCompleto());
    }

    @Test
    void eliminarMascota_ShouldReturnNoContent() {
        doNothing().when(mascotaService).eliminarMascota(1L);

        ResponseEntity<Void> response = mascotaController.eliminarMascota(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(mascotaService, times(1)).eliminarMascota(1L);
    }

    @Test
    void crearMascota_WhenServiceThrowsException_ShouldReturn500() {
        when(mascotaService.crearMascota(requestDTO)).thenThrow(new RuntimeException("Error al crear mascota"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            mascotaController.crearMascota(requestDTO);
        });

        assertEquals("Error al crear mascota", thrown.getMessage());
    }
}
