package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Service.RecuperarContrasenaTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final RecuperarContrasenaTokenService recuperarContrasenaTokenService;
    @PostMapping("/sendMail")
    public ResponseEntity<Void> enviarRecuperarContrasenaEmail(@RequestBody Map<String, String> request) throws Exception {
        String email = request.get("email");
        recuperarContrasenaTokenService.createAndSendPasswordResetToken(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/reset/check/{token}")
    public ResponseEntity<Boolean> verificarTokenValido(@PathVariable String token) {
        boolean isValid = recuperarContrasenaTokenService.isValidToken(token);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }
    @PostMapping("/reset/{token}")
    public ResponseEntity<Void> recuperarContrasena(@PathVariable String token, @RequestBody Map<String, String> request){
        String newContrasena = request.get("newContrasena");
        recuperarContrasenaTokenService.recuperarContrasena(token, newContrasena);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
