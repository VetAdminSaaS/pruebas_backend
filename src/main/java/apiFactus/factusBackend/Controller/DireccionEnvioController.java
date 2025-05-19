package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.DireccionEnvioDTO;
import apiFactus.factusBackend.Service.DireccionEnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/direccion/envio")
public class DireccionEnvioController {

    private final DireccionEnvioService direccionEnvioService;

    @GetMapping
    public ResponseEntity<List<DireccionEnvioDTO>> listar() {
        List<DireccionEnvioDTO> direccionEnvioDTOS = direccionEnvioService.getAll();
        return ResponseEntity.ok(direccionEnvioDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionEnvioDTO> obtener(@PathVariable Long id) {
        DireccionEnvioDTO direccionEnvioDTO = direccionEnvioService.findById(id);
        return ResponseEntity.ok(direccionEnvioDTO);
    }

    @PostMapping
    public ResponseEntity<DireccionEnvioDTO> crear(@RequestBody @Valid DireccionEnvioDTO direccionEnvioDTO) {
        DireccionEnvioDTO direccionEnvioDTOcrear = direccionEnvioService.create(direccionEnvioDTO);
        return new ResponseEntity<>(direccionEnvioDTOcrear, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")

    public ResponseEntity<DireccionEnvioDTO> updateDireccion(@RequestBody @Valid DireccionEnvioDTO direccionEnvioDTO, @PathVariable Long id) {
        DireccionEnvioDTO direccionEnvioDTOupdate = direccionEnvioService.update(id, direccionEnvioDTO);
        return ResponseEntity.ok(direccionEnvioDTOupdate);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        direccionEnvioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> manejarExcepciones(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
