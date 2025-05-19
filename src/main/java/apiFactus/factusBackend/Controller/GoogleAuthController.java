package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Dto.GoogleUserInfo;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Security.TokenProvider;
import apiFactus.factusBackend.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final UsuarioService userService;
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthController.class);

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        log.info("Código de Google recibido: {}", code);

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Código de Google no recibido");
        }

        String googleToken = obtenerTokenDeGoogle(code);
        log.info("Token de acceso de Google: {}", googleToken);

        if (googleToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo obtener token de Google");
        }

        GoogleUserInfo googleUser = obtenerDatosUsuario(googleToken);
        log.info("Datos del usuario de Google: {}", googleUser);

        if (googleUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo obtener información del usuario");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(googleUser.getEmail());
        Usuario usuario;

        if (usuarioOptional.isPresent()) {
            usuario = usuarioOptional.get();
        } else {
            log.info("Creando nuevo usuario: {}", googleUser.getEmail());
            usuario = userService.createUserWithGoogle(googleUser.getEmail(), googleUser.getName());
        }


        log.info("Usuario autenticado: {}", usuario.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = tokenProvider.createAccessToken(authentication);

        log.info("JWT generado: {}", jwt);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("email", googleUser.getEmail());
        response.put("name", googleUser.getName());

        return ResponseEntity.ok(response);
    }


    private String obtenerTokenDeGoogle(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info("Enviando solicitud a Google para obtener el token...");
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);
            log.info("Respuesta de Google: {}", response.getBody());

            if (response.getBody() != null && response.getBody().containsKey("access_token")) {
                return response.getBody().get("access_token").toString();
            } else {
                log.error("No se recibió un token de acceso en la respuesta de Google");
                return null;
            }
        } catch (Exception e) {
            log.error("Error al obtener el token de Google: ", e);
            return null;
        }



    }


    private GoogleUserInfo obtenerDatosUsuario(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        log.info("Solicitando datos del usuario de Google...");

        try {
            ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserInfo.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error HTTP al obtener datos del usuario: Status {} - {}", e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener datos del usuario de Google: ", e);
        }
        return null;
    }





        @GetMapping("/success")
    public ResponseEntity<?> googleLoginSuccess(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo autenticar con Google");
        }

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");


        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {

            userService.createUserWithGoogle(email, name);
            userDetails = userDetailsService.loadUserByUsername(email);
        }


        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = tokenProvider.createAccessToken(newAuth);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("email", email);
        response.put("name", name);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/error")
    public ResponseEntity<String> googleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error en la autenticación con Google");
    }


}
