package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.CategoriaDTO;
import apiFactus.factusBackend.Service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categorias")
@PreAuthorize("hasRole('ADMIN')")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> getAllCategorias(){
        List<CategoriaDTO> categorias = categoriaService.getAll();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> getCategoriaById(@PathVariable Integer id){
        CategoriaDTO categoriaDTO = categoriaService.findById(id);
        return new ResponseEntity<>(categoriaDTO, HttpStatus.OK);
    }
    @PostMapping("/crear")
    public ResponseEntity<CategoriaDTO> createCategoria(@RequestBody CategoriaDTO categoriaDTO){
        CategoriaDTO categoriaDTOCreated = categoriaService.create(categoriaDTO);
        return new ResponseEntity<>(categoriaDTOCreated, HttpStatus.CREATED);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<CategoriaDTO> updateCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO, @PathVariable Integer id){
        CategoriaDTO categoriaupdate = categoriaService.update(id, categoriaDTO);
        return new ResponseEntity<>(categoriaupdate, HttpStatus.OK);
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Integer id){
        categoriaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
