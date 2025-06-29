package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.DireccionEnvioController;
import apiFactus.factusBackend.Dto.DireccionEnvioDTO;
import apiFactus.factusBackend.Service.DireccionEnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DireccionEnvioControllerTest {

    private DireccionEnvioService direccionEnvioService;
    private DireccionEnvioController controller;

    @BeforeEach
    void setUp() {
        direccionEnvioService = mock(DireccionEnvioService.class);
        controller = new DireccionEnvioController(direccionEnvioService);
    }

    @Test
    void testListar() {
        DireccionEnvioDTO dto = new DireccionEnvioDTO();
        dto.setId(1L);
        dto.setDireccion("Av. Ejemplo");
        when(direccionEnvioService.getAll()).thenReturn(List.of(dto));

        var response = controller.listar();

        assertEquals(1, response.getBody().size());
        assertEquals("Av. Ejemplo", response.getBody().get(0).getDireccion());
        verify(direccionEnvioService).getAll();
    }

    @Test
    void testObtener() {
        DireccionEnvioDTO dto = new DireccionEnvioDTO();
        dto.setId(2L);
        dto.setCiudad("Trujillo");
        when(direccionEnvioService.findById(2L)).thenReturn(dto);

        var response = controller.obtener(2L);

        assertEquals("Trujillo", response.getBody().getCiudad());
        verify(direccionEnvioService).findById(2L);
    }

    @Test
    void testCrear() {
        DireccionEnvioDTO input = new DireccionEnvioDTO();
        input.setDireccion("Jr. Lima");
        DireccionEnvioDTO saved = new DireccionEnvioDTO();
        saved.setId(3L);
        saved.setDireccion("Jr. Lima");

        when(direccionEnvioService.create(input)).thenReturn(saved);

        var response = controller.crear(input);

        assertEquals(3L, response.getBody().getId());
        assertEquals("Jr. Lima", response.getBody().getDireccion());
        verify(direccionEnvioService).create(input);
    }

    @Test
    void testUpdateDireccion() {
        DireccionEnvioDTO updated = new DireccionEnvioDTO();
        updated.setId(4L);
        updated.setTelefono("999999999");

        when(direccionEnvioService.update(4L, updated)).thenReturn(updated);

        var response = controller.updateDireccion(updated, 4L);

        assertEquals("999999999", response.getBody().getTelefono());
        verify(direccionEnvioService).update(4L, updated);
    }

    @Test
    void testEliminar() {
        doNothing().when(direccionEnvioService).delete(5L);

        var response = controller.eliminar(5L);

        assertEquals(204, response.getStatusCodeValue());
        verify(direccionEnvioService).delete(5L);
    }

    @Test
    void testManejarExcepciones() {
        var response = controller.manejarExcepciones(new RuntimeException("Error de prueba"));
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error de prueba", response.getBody());
    }
}
