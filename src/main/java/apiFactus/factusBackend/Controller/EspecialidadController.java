package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Domain.Entity.Especialidad;
import apiFactus.factusBackend.Dto.EspecialidadDTO;
import apiFactus.factusBackend.Service.EspecialidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/especialidades")
@PreAuthorize("hasRole('ADMIN')")
public class EspecialidadController {
    private final EspecialidadService especialidadService;

    @GetMapping
    public ResponseEntity<List<EspecialidadDTO>> getAllEspecialidades() {
        List<EspecialidadDTO> especialidades = especialidadService.getAllEspecialidad();
        return new ResponseEntity<>(especialidades, HttpStatus.OK);
    }
    @GetMapping("/page")
    public ResponseEntity<Page<EspecialidadDTO>> getAllEspecialidades(@PageableDefault(size = 10, sort = "nombre")Pageable pageable) {
        Page<EspecialidadDTO>especialidadDTOS = especialidadService.getAllEspecialidadDTO(pageable);
        return new ResponseEntity<>(especialidadDTOS, HttpStatus.OK);

    }
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidadeById(@PathVariable Long id) {
        EspecialidadDTO especialidad = especialidadService.findById(id);
        return new ResponseEntity<>(especialidad, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<EspecialidadDTO> createEspecialidade(@Valid @RequestBody EspecialidadDTO especialidadDTO) {
        EspecialidadDTO especialidad = especialidadService.createEspecialidad(especialidadDTO);
        return new ResponseEntity<>(especialidad, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadDTO> updateEspecialidade(@Valid @RequestBody EspecialidadDTO especialidadDTO, @PathVariable Long id) {
        EspecialidadDTO especialidad = especialidadService.updateEspecialidad(id, especialidadDTO);
        return new ResponseEntity<>(especialidad, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialidade(@PathVariable Long id) throws BadRequestException {
        especialidadService.deleteEspecialidad(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
