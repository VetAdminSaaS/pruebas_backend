package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.UserProfileDTO;
import apiFactus.factusBackend.Dto.UsuariosStoreDTO;
import apiFactus.factusBackend.Service.Impl.UserServiceImpl;
import apiFactus.factusBackend.Service.StoreService;
import apiFactus.factusBackend.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/usuarios")
public class StoreController {
    private final StoreService storeService;
    private final UsuarioService usuarioService;

    @GetMapping("/listar")
    @PreAuthorize("hasRole('CUSTOMER')")
    public UsuariosStoreDTO obtenerMiPerfil() {
        return storeService.obtenerUsuarioAutenticado();
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileDTO>> list() {
        List<UserProfileDTO> usuarios = storeService.findAll();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
    @GetMapping("/total")
    public ResponseEntity<Long> totalUsuarios() {
        long totalUsuarios = usuarioService.getTotalUsuarios();
        return new ResponseEntity<>(totalUsuarios, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDTO> updateProfileUsuario(@PathVariable Integer id, @RequestBody UserProfileDTO userProfileDTO) {
        UserProfileDTO userProfileDTO1 = usuarioService.updateUserProfile(id, userProfileDTO);
        return new ResponseEntity<>(userProfileDTO1, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> obtenerUsuario(@PathVariable Integer id) {
        UserProfileDTO userProfileDTO = usuarioService.getUserProfileById(id);
        return new ResponseEntity<>(userProfileDTO, HttpStatus.OK);
    }
}
