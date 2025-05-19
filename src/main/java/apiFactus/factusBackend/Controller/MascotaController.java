package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Domain.Entity.Mascota;
import apiFactus.factusBackend.Dto.MascotaRequestDTO;
import apiFactus.factusBackend.Dto.MascotaResponseDTO;
import apiFactus.factusBackend.Service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mascotas")
public class MascotaController {
    private final MascotaService mascotaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('APODERADO','ADMIN','ASISTENTE')")
    public ResponseEntity<MascotaResponseDTO> crearMascota(@Valid @RequestBody MascotaRequestDTO mascotaRequestDTO) {
        MascotaResponseDTO responseDTO = mascotaService.crearMascota(mascotaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('APODERADO','ADMIN','ASISTENTE')")
    public ResponseEntity<MascotaResponseDTO> consultarMascota(@PathVariable Long id) {
        MascotaResponseDTO mascotaResponseDTO = mascotaService.obtenerMascotaPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(mascotaResponseDTO);
    }
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASISTENTE')")
    public ResponseEntity<List<MascotaResponseDTO>> listarMascotas() {
        List<MascotaResponseDTO> mascotas = mascotaService.listarMascotas();
        return ResponseEntity.ok(mascotas);
    }
    @GetMapping
    @PreAuthorize("hasRole('APODERADO')")
    public ResponseEntity<List<MascotaResponseDTO>> listarMascotasByApoderado() {
        List<MascotaResponseDTO> mascotas = mascotaService.listarMascotasByApoderado();
        return ResponseEntity.ok(mascotas);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('APODERADO', 'ADMIN', 'ASISTENTE')")
    public ResponseEntity<MascotaResponseDTO> actualizarMascota(
            @PathVariable Long id,
            @Valid @RequestBody MascotaRequestDTO mascotaRequestDTO) {
        MascotaResponseDTO updatedMascota = mascotaService.actualizarMascota(id, mascotaRequestDTO);
        return ResponseEntity.ok(updatedMascota);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('APODERADO', 'ADMIN', 'ASISTENTE')")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }






}
