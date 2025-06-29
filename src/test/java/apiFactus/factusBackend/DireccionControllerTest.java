package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.DireccionEnvioController;
import apiFactus.factusBackend.Dto.DireccionEnvioDTO;
import apiFactus.factusBackend.Service.DireccionEnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class DireccionControllerTest {

    @Mock
    private DireccionEnvioService direccionEnvioService;

    @InjectMocks
    private DireccionEnvioController direccionEnvioController;

    private DireccionEnvioDTO direccion;

    @BeforeEach
    void setUp() {
        direccion = new DireccionEnvioDTO();
        direccion.setId(1L);
        direccion.setCiudad("Trujillo");
        direccion.setDireccion("Av. América Sur 123");
    }

    // Escenario 1: Lista con elementos
    @Test
    void testListarDirecciones_OK() {
        when(direccionEnvioService.getAll()).thenReturn(List.of(direccion));

        ResponseEntity<List<DireccionEnvioDTO>> response = direccionEnvioController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Trujillo", response.getBody().get(0).getCiudad());
        verify(direccionEnvioService).getAll();
    }

    // Escenario 2: Lista vacía
    @Test
    void testListarDirecciones_Vacio() {
        when(direccionEnvioService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<DireccionEnvioDTO>> response = direccionEnvioController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // Escenario 3: Obtener por ID válido
    @Test
    void testObtenerDireccionPorId_OK() {
        when(direccionEnvioService.findById(1L)).thenReturn(direccion);

        ResponseEntity<DireccionEnvioDTO> response = direccionEnvioController.obtener(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Trujillo", response.getBody().getCiudad());
        verify(direccionEnvioService).findById(1L);
    }

    // Escenario 4: Crear nueva dirección
    @Test
    void testCrearDireccion_OK() {
        when(direccionEnvioService.create(direccion)).thenReturn(direccion);

        ResponseEntity<DireccionEnvioDTO> response = direccionEnvioController.crear(direccion);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Trujillo", response.getBody().getCiudad());
        verify(direccionEnvioService).create(direccion);
    }

    // Escenario 5: Eliminar dirección
    @Test
    void testEliminarDireccion_OK() {
        doNothing().when(direccionEnvioService).delete(1L);

        ResponseEntity<Void> response = direccionEnvioController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(direccionEnvioService).delete(1L);
    }
}
