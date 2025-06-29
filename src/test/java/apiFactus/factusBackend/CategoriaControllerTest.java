package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.CategoriaController;
import apiFactus.factusBackend.Dto.CategoriaDTO;
import apiFactus.factusBackend.Service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaControllerTest {

    private CategoriaService categoriaService;
    private CategoriaController categoriaController;

    @BeforeEach
    void setUp() {
        categoriaService = mock(CategoriaService.class);
        categoriaController = new CategoriaController(categoriaService);
    }

    @Test
    void testCreateCategoria() {
        // Arrange
        CategoriaDTO request = new CategoriaDTO();
        request.setNombre("Alimentos");
        request.setDescripcion("Productos alimenticios para mascotas");

        CategoriaDTO expected = new CategoriaDTO();
        expected.setId(1);
        expected.setNombre("Alimentos");
        expected.setDescripcion("Productos alimenticios para mascotas");

        when(categoriaService.create(request)).thenReturn(expected);

        // Act
        ResponseEntity<CategoriaDTO> response = categoriaController.createCategoria(request);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(expected, response.getBody());
    }

    @Test
    void testGetAllCategorias() {
        // Arrange
        CategoriaDTO categoria = new CategoriaDTO();
        categoria.setId(1);
        categoria.setNombre("Accesorios");
        categoria.setDescripcion("Productos varios");

        List<CategoriaDTO> expectedList = Arrays.asList(categoria);
        when(categoriaService.getAll()).thenReturn(expectedList);

        // Act
        ResponseEntity<List<CategoriaDTO>> response = categoriaController.getAllCategorias();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedList, response.getBody());
    }

    @Test
    void testGetCategoriaById() {
        // Arrange
        int id = 1;
        CategoriaDTO expected = new CategoriaDTO();
        expected.setId(id);
        expected.setNombre("Salud");
        expected.setDescripcion("Productos de cuidado para mascotas");

        when(categoriaService.findById(id)).thenReturn(expected);

        // Act
        ResponseEntity<CategoriaDTO> response = categoriaController.getCategoriaById(id);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    void testUpdateCategoria() {
        // Arrange
        int id = 1;
        CategoriaDTO request = new CategoriaDTO();
        request.setNombre("Actualizado");
        request.setDescripcion("Descripción actualizada");

        CategoriaDTO expected = new CategoriaDTO();
        expected.setId(id);
        expected.setNombre("Actualizado");
        expected.setDescripcion("Descripción actualizada");

        when(categoriaService.update(id, request)).thenReturn(expected);

        // Act
        ResponseEntity<CategoriaDTO> response = categoriaController.updateCategoria(request, id);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    void testDeleteCategoria() {
        // Arrange
        int id = 1;
        doNothing().when(categoriaService).delete(id);

        // Act
        ResponseEntity<Void> response = categoriaController.deleteCategoria(id);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(categoriaService, times(1)).delete(id);
    }
}
