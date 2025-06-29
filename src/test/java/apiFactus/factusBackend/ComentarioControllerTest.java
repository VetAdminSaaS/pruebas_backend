package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.ComentarioController;
import apiFactus.factusBackend.Dto.ComentarioRequestDTO;
import apiFactus.factusBackend.Service.ComentarioProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioControllerTest {

    @Mock
    private ComentarioProductoService comentarioProductoService;

    @InjectMocks
    private ComentarioController comentarioController;

    private ComentarioRequestDTO comentarioRequestDTO;
    private List<ComentarioRequestDTO> comentariosList;
    private Page<ComentarioRequestDTO> comentariosPage;

    @BeforeEach
    void setUp() {
        comentarioRequestDTO = new ComentarioRequestDTO();
        comentarioRequestDTO.setRating(5);
        comentarioRequestDTO.setComentario("Excelente producto");
        comentarioRequestDTO.setUsuarioId(1);
        comentarioRequestDTO.setNombreUsuario("usuario1");
        comentarioRequestDTO.setCreatedAt(LocalDateTime.now());

        comentariosList = Collections.singletonList(comentarioRequestDTO);
        comentariosPage = new PageImpl<>(comentariosList);
    }

    @Test
    void getAllComentarioPorProducto_ShouldReturnListOfComentarios() {
        // Arrange
        when(comentarioProductoService.getAllComentariosPorProducto(anyInt()))
                .thenReturn(comentariosList);

        // Act
        ResponseEntity<List<ComentarioRequestDTO>> response =
                comentarioController.getAllComentarioPorProducto(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comentariosList, response.getBody());
        verify(comentarioProductoService, times(1)).getAllComentariosPorProducto(anyInt());
    }

    @Test
    void paginateComentario_ShouldReturnPageOfComentarios() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        when(comentarioProductoService.getComentariosPorProducto(anyInt(), any(Pageable.class)))
                .thenReturn(comentariosPage);

        // Act
        ResponseEntity<Page<ComentarioRequestDTO>> response =
                comentarioController.paginateComentario(1, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comentariosPage, response.getBody());
        verify(comentarioProductoService, times(1)).getComentariosPorProducto(anyInt(), any(Pageable.class));
    }

    @Test
    void getComentario_ShouldReturnComentario() {
        // Arrange
        when(comentarioProductoService.finById(anyInt()))
                .thenReturn(comentarioRequestDTO);

        // Act
        ResponseEntity<ComentarioRequestDTO> response =
                comentarioController.getComentario(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comentarioRequestDTO, response.getBody());
        verify(comentarioProductoService, times(1)).finById(anyInt());
    }

    @Test
    void createComentario_ShouldReturnCreatedComentario() {
        // Arrange
        when(comentarioProductoService.create(any(ComentarioRequestDTO.class), anyInt()))
                .thenReturn(comentarioRequestDTO);

        // Act
        ResponseEntity<ComentarioRequestDTO> response =
                comentarioController.createComentario(comentarioRequestDTO, 1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(comentarioRequestDTO, response.getBody());
        verify(comentarioProductoService, times(1)).create(any(ComentarioRequestDTO.class), anyInt());
    }

    @Test
    void deleteComentario_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(comentarioProductoService).delete(anyInt());

        // Act
        ResponseEntity<Void> response = comentarioController.deleteComentario(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(comentarioProductoService, times(1)).delete(anyInt());
    }

    @Test
    void obtenerPromedio_ShouldReturnAverageRating() {
        // Arrange
        double promedioEsperado = 4.5;
        when(comentarioProductoService.obtenePromedioRatingProducto(anyInt()))
                .thenReturn(promedioEsperado);

        // Act
        ResponseEntity<Double> response =
                comentarioController.obtenerPromedio(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(promedioEsperado, response.getBody());
        verify(comentarioProductoService, times(1)).obtenePromedioRatingProducto(anyInt());
    }
}