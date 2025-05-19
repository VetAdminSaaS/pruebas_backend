package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.Entity.recuperarContrasenaToken;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Service.RecuperarContrasenaTokenService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Service
public class recuperarContrasenaServiceImpl implements RecuperarContrasenaTokenService {
    private final UsuarioRepository usuarioRepository;
    private final apiFactus.factusBackend.Repository.recuperarContrasenaTokenRepository recuperarContrasenaTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Value("${vet.clinic.frontend}")
    private String vetClinicFrontend;
    private static final Logger logger = LoggerFactory.getLogger(recuperarContrasenaServiceImpl.class);

    public recuperarContrasenaServiceImpl(UsuarioRepository usuarioRepository, apiFactus.factusBackend.Repository.recuperarContrasenaTokenRepository recuperarContrasenaTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.recuperarContrasenaTokenRepository = recuperarContrasenaTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void createAndSendPasswordResetToken(String email) throws Exception {
        String cleanedEmail = email.trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(cleanedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        recuperarContrasenaTokenRepository.deleteByUsuario(usuario);
        recuperarContrasenaToken nuevoToken = new recuperarContrasenaToken();
        nuevoToken.setToken(UUID.randomUUID().toString());
        nuevoToken.setUsuario(usuario);
        nuevoToken.setExpiration(LocalDateTime.now().plusMinutes(10));
        nuevoToken = recuperarContrasenaTokenRepository.save(nuevoToken);
        String resetUrl = vetClinicFrontend + "/forgot?token=" + nuevoToken.getToken();
        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario.getEmail());
        model.put("resetUrl", resetUrl);
        Mail mail = emailService.createMail(
                usuario.getEmail(),
                "Restablecer Contraseña",
                model,
                mailFrom
        );

        emailService.sendEmail(mail, "email/recuperar-contrasena-template.html");
    }



    @Override
    public recuperarContrasenaToken findByToken(String token){
        return recuperarContrasenaTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));
    }
    @Override
    public void eliminarRecuperacionToken(recuperarContrasenaToken recuperarContrasenaToken){
        recuperarContrasenaTokenRepository.delete(recuperarContrasenaToken);
    }
    @Override
    public boolean isValidToken(String token) {
        return recuperarContrasenaTokenRepository.findByToken(token)
                .filter(t->!t.isExpired())
                .isPresent();
    }
    @Override
    public void recuperarContrasena(String token, String newContrasena) {
        recuperarContrasenaToken recuperarContrasenaToken = recuperarContrasenaTokenRepository.findByToken(token)
                .filter(t->!t.isExpired())
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));
        Usuario usuario = recuperarContrasenaToken.getUsuario();
        if (newContrasena == null || newContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }
        usuario.setPassword(passwordEncoder.encode(newContrasena));
        usuarioRepository.save(usuario);
        recuperarContrasenaTokenRepository.delete(recuperarContrasenaToken);
    }

}
