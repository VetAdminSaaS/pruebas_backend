package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.ServiciosVeterinariosDTO;
import apiFactus.factusBackend.Service.ServiciosVeterinariosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class ServiciosVeterinariosController {
    private final ServiciosVeterinariosService serviciosVeterinariosService;


    @GetMapping
    public ResponseEntity<List<ServiciosVeterinariosDTO>> getAllServicios() {
        List<ServiciosVeterinariosDTO> serviciosVeterinariosDTOS = serviciosVeterinariosService.getAllServiciosVeterinarios();
        return new ResponseEntity<>(serviciosVeterinariosDTOS, HttpStatus.OK);
    }
    @GetMapping("/page")
    public ResponseEntity<Page<ServiciosVeterinariosDTO>> getAllServicios(@PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<ServiciosVeterinariosDTO> serviciosVeterinariosDTOS = serviciosVeterinariosService.getAllServiciosVeterinarios(pageable);
        return new ResponseEntity<>(serviciosVeterinariosDTOS, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ServiciosVeterinariosDTO> getServicioById(@PathVariable Long id) {
        ServiciosVeterinariosDTO serviciosVeterinariosDTO = serviciosVeterinariosService.findbyId(id);
        return new ResponseEntity<>(serviciosVeterinariosDTO, HttpStatus.OK);
    }
    @PostMapping("/crear")
    public ResponseEntity<ServiciosVeterinariosDTO> crearServicio(@RequestBody ServiciosVeterinariosDTO serviciosVeterinariosDTO) {
        ServiciosVeterinariosDTO serviciosVeterinariosDTOCrear = serviciosVeterinariosService.crearServicioVeterinario(serviciosVeterinariosDTO);
        return new ResponseEntity<>(serviciosVeterinariosDTOCrear, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ServiciosVeterinariosDTO> updateServicios(@Valid @RequestBody ServiciosVeterinariosDTO serviciosVeterinariosDTO, @PathVariable Long id) {
        ServiciosVeterinariosDTO serviciosVeterinariosDTOupdate = serviciosVeterinariosService.updateServicioVeterinario(id, serviciosVeterinariosDTO);
        return new ResponseEntity<>(serviciosVeterinariosDTOupdate, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicios(@PathVariable Long id) {
        serviciosVeterinariosService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
