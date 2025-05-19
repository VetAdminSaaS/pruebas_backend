package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.InvitadoDTO;
import apiFactus.factusBackend.Dto.MesaDTO;
import apiFactus.factusBackend.Repository.InvitadoRepository;
import apiFactus.factusBackend.Service.InvitadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import apiFactus.factusBackend.Domain.Entity.Invitado;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@Controller
@RequestMapping("/mesa")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MesaController {
    private final InvitadoService invitadoService;
    private final InvitadoRepository invitadoRepository;


    @PutMapping("/invitados/{invitadoId}/asignar-mesa/{mesaId}")
    public ResponseEntity<InvitadoDTO> asignarMesa(@PathVariable Long invitadoId, @PathVariable Long mesaId) {
        InvitadoDTO invitadoActualizado = invitadoService.asignarMesa(invitadoId, mesaId);
        return ResponseEntity.ok(invitadoActualizado);
    }

    @GetMapping
    public ResponseEntity<List<MesaDTO>> getAllMesas() {
        List<MesaDTO> mesas = invitadoService.getAllMesa();
        return ResponseEntity.ok(mesas);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> getMesaById(@PathVariable Long id) {
        MesaDTO mesa = invitadoService.findMesaById(id);
        return ResponseEntity.ok(mesa);
    }


    @PostMapping
    public ResponseEntity<MesaDTO> createMesa(@RequestBody MesaDTO mesaDTO) {
        MesaDTO nuevaMesa = invitadoService.create(mesaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMesa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> updateMesa(@PathVariable Long id, @RequestBody MesaDTO mesaDTO) {
        MesaDTO mesaActualizada = invitadoService.update(id, mesaDTO);
        return ResponseEntity.ok(mesaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long id) {
        invitadoService.deleteMesa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{numero}/invitados")
    public ResponseEntity<List<String>> obtenerNombresInvitadosPorMesa(@PathVariable int numero) {
        List<String> nombres = invitadoRepository.findByMesaNumero(numero)
                .stream()
                .map(Invitado::getNombre)
                .collect(Collectors.toList());

        if (nombres.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(nombres);
    }

    @PutMapping("/retirar/{nombre}")
    public ResponseEntity<InvitadoDTO> retirarDeMesa(@PathVariable String nombre) {
        InvitadoDTO invitadoDTO = invitadoService.retirarDeMesaPorNombre(nombre);
        return ResponseEntity.ok(invitadoDTO);
    }

}
