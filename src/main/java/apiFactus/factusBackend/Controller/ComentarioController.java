package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.ComentarioRequestDTO;
import apiFactus.factusBackend.Service.ComentarioProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentario")
@RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioProductoService comentarioProductoService;

    @GetMapping("/producto/{productoId}")

    public ResponseEntity<List<ComentarioRequestDTO>> getAllComentarioPorProducto(@PathVariable Long productoId) {
        List<ComentarioRequestDTO> comentarioRequestDTOS = comentarioProductoService.getAllComentariosPorProducto(Math.toIntExact(productoId));
        return new ResponseEntity<>(comentarioRequestDTOS, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ComentarioRequestDTO>> paginateComentario(
            @RequestParam Integer productoId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ComentarioRequestDTO> comentarios = comentarioProductoService.getComentariosPorProducto(productoId, pageable);
        return ResponseEntity.ok(comentarios);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ComentarioRequestDTO> getComentario(@PathVariable Integer id) {
        ComentarioRequestDTO comentarioRequestDTO = comentarioProductoService.finById(id);
        return new ResponseEntity<>(comentarioRequestDTO, HttpStatus.OK);
    }
    @PostMapping("/producto/{productoId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ComentarioRequestDTO> createComentario(@Valid @RequestBody ComentarioRequestDTO comentarioRequestDTO, @PathVariable Integer productoId) {
        ComentarioRequestDTO newComentario = comentarioProductoService.create(comentarioRequestDTO, productoId);
        return new ResponseEntity<>(newComentario, HttpStatus.CREATED);
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Integer id) {
        comentarioProductoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/producto/promedio/{productoId}")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Integer productoId) {
        Double promedio = comentarioProductoService.obtenePromedioRatingProducto(productoId);
        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }


}
