package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.AuthResponse;
import apiFactus.factusBackend.Dto.LoginDTO;
import apiFactus.factusBackend.Dto.UserProfileDTO;
import apiFactus.factusBackend.Dto.UserRegistrationDTO;
import apiFactus.factusBackend.Service.TokenReactivationService;
import apiFactus.factusBackend.Service.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioService usuarioService;
    private final TokenReactivationService tokenReactivationService;

    @PostMapping("/register/customer")
    public ResponseEntity<UserProfileDTO> registerCustomer(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserProfileDTO userProfile = usuarioService.registerCustomer(userRegistrationDTO);
        return new ResponseEntity<>(userProfile, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) throws MessagingException {
        AuthResponse authResponse = usuarioService.login(loginDTO);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    @PutMapping("/suspender/{id}")
    public ResponseEntity<?> suspenderCuenta(@PathVariable Integer id) {
        try {
            usuarioService.suspendercuenta(id);
            return ResponseEntity.ok().body("Cuenta suspendida exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al suspender la cuenta");
        }
    }

    @PostMapping("/reactivar")
    public ResponseEntity<?> reactivarCuenta(@RequestParam Map<String, String> request) {
        String token = request.get("token");

        boolean reactivado = usuarioService.reactivarCuenta(token);

        if (reactivado) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Tu cuenta ha sido reactivada con éxito."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "El enlace de reactivación no es válido o ha expirado.Por favor vuelve a solicitar una reactivación"));
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarCuenta(@PathVariable Integer id) throws MessagingException {
        usuarioService.eliminarCuenta(id);
        return new ResponseEntity<>("ELIMINATED", HttpStatus.OK);
    }
    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verificarEmpleado(@PathVariable String token) throws BadRequestException {
        usuarioService.verificarCuenta(token);
        return ResponseEntity.ok("Cuenta verificada con éxito. Ahora puedes completar tu información.");
    }

}
