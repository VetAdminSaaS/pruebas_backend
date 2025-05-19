package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Domain.Entity.Apoderado;
import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Dto.ApoderadoAdminDTO;
import apiFactus.factusBackend.Dto.ApoderadoDTO;
import apiFactus.factusBackend.Dto.ApoderadoResponseDTO;
import apiFactus.factusBackend.Service.ApoderadoService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apoderado")
public class ApoderadoController {

    private final ApoderadoService apoderadoService;

    @PostMapping
    public ResponseEntity<ApoderadoResponseDTO> crearApoderado(@Valid @RequestBody ApoderadoDTO apoderado) throws BadRequestException {
        ApoderadoResponseDTO apoderadocreate = apoderadoService.crearApoderado(apoderado);
        return new ResponseEntity<>(apoderadocreate, HttpStatus.CREATED);

    }
    @GetMapping("/{id}")
    public ResponseEntity<ApoderadoResponseDTO> getApoderadoById(@PathVariable("id") Long id) throws BadRequestException {
        ApoderadoResponseDTO apoderado = apoderadoService.obtenerDetallesDeApoderado(id);
        return new ResponseEntity<>(apoderado, HttpStatus.OK);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASISTENTE')")
    public ResponseEntity<List<ApoderadoResponseDTO>> getAllApoderados() throws BadRequestException {
        List<ApoderadoResponseDTO> apoderado = apoderadoService.obtenerApoderados();
        return new ResponseEntity<>(apoderado, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASISTENTE','APODERADO')")
    public ResponseEntity<ApoderadoResponseDTO> updateApoderado(@PathVariable("id") Long id, @RequestBody ApoderadoDTO apoderado) {
        ApoderadoResponseDTO apoderadoResponseupdate = apoderadoService.actualizarApoderado(id, apoderado);
        return new ResponseEntity<>(apoderadoResponseupdate, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASISTENTE')")
    public ResponseEntity<Void> eliminarapoderado(@PathVariable("id") Long id) throws BadRequestException {
        apoderadoService.eliminarApoderado(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN','ASISTENTE')")
    public ResponseEntity<ApoderadoResponseDTO> crearApoderadoByAdmin(@RequestBody ApoderadoAdminDTO apoderado) throws BadRequestException, MessagingException {
        ApoderadoResponseDTO apoderadoResponseDTO = apoderadoService.crearApoderadoByAdmin(apoderado, ERole.APODERADO);
        return new ResponseEntity<>(apoderadoResponseDTO, HttpStatus.CREATED);
    }

}
