package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.EmpleadoProfileDTO;
import apiFactus.factusBackend.Dto.EmpleadoRegistrationDTO;
import apiFactus.factusBackend.Dto.EmpleadosDTO;
import apiFactus.factusBackend.Dto.EmpleadosDetailsDTO;
import apiFactus.factusBackend.Service.EmpleadoService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/empleados")
@PreAuthorize("hasRole('ADMIN')")
public class EmpleadoAdminController {
    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<EmpleadosDetailsDTO>> empleadosListALL(){
        List<EmpleadosDetailsDTO> empleadosDetailsDTOS = empleadoService.getAll();
        return new ResponseEntity<>(empleadosDetailsDTOS, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<EmpleadoRegistrationDTO> create(@Valid @RequestBody EmpleadoRegistrationDTO empleadosDTO) throws MessagingException, BadRequestException {
        EmpleadoRegistrationDTO empleadosDTOcrear = empleadoService.crearEmpleado(empleadosDTO);
        return new ResponseEntity<>(empleadosDTOcrear, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadosDetailsDTO> empleadosDetailsByID(@PathVariable Long id){
        EmpleadosDetailsDTO empleadosDetailsDTO = empleadoService.findById(id);
        return new ResponseEntity<>(empleadosDetailsDTO, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadosDetailsDTO> update(@PathVariable Long id, @Valid @RequestBody EmpleadosDetailsDTO empleadosDetailsDTO){
        EmpleadosDetailsDTO empleadosDetailsDTOUpdate = empleadoService.update(id, empleadosDetailsDTO);
        return new ResponseEntity<>(empleadosDetailsDTOUpdate, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
     empleadoService.delete(id);
     return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoProfileDTO> register(@Valid @RequestBody EmpleadoRegistrationDTO empleadoRegistrationDTO) throws MessagingException, BadRequestException {
        System.out.println("Datos recibidos en el backend: " + empleadoRegistrationDTO);
        EmpleadoProfileDTO empleadoProfileDTO = empleadoService.registroEmpleadoVeterinario(empleadoRegistrationDTO);
        return new ResponseEntity<>(empleadoProfileDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/completar-registro")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<EmpleadosDTO> completarRegistro(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadosDTO empleadosDTO) {

        EmpleadosDTO empleadoActualizado = empleadoService.completarRegistro(id, empleadosDTO);
        return ResponseEntity.ok(empleadoActualizado);
    }

}
