package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.InvitadoDTO;
import apiFactus.factusBackend.Service.InvitadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitados")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InvitadoController {

    private final InvitadoService invitadoService;

    @GetMapping
    public ResponseEntity<List<InvitadoDTO>> getAll() {
        return ResponseEntity.ok(invitadoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvitadoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(invitadoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InvitadoDTO> create(@RequestBody InvitadoDTO invitadoDTO) {
        return ResponseEntity.ok(invitadoService.create(invitadoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvitadoDTO> update(@PathVariable Long id, @RequestBody InvitadoDTO invitadoDTO) {
        return ResponseEntity.ok(invitadoService.update(id, invitadoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invitadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}