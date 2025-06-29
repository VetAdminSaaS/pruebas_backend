package apiFactus.factusBackend.Security;

import apiFactus.factusBackend.exception.RoleNotFoundException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.SignatureAlgorithm;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.validity-in-seconds}")
    private long jwtValidityInSeconds;

    @Value("${jwt.private-key}") // Ejemplo: "classpath:keys/private_key.pem"
    private String privateKeyPath;

    @Value("${jwt.public-key}")  // Ejemplo: "classpath:keys/public_key.pem"
    private String publicKeyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey(privateKeyPath);
            this.publicKey = loadPublicKey(publicKeyPath);
        } catch (Exception e) {
            throw new IllegalStateException("Error cargando claves RSA para JWT", e);
        }
    }

    private PrivateKey loadPrivateKey(String resourcePath) throws Exception {
        Resource resource = resourceLoader.getResource(resourcePath);
        byte[] keyBytes = readAllBytes(resource);

        String key = new String(keyBytes)
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey loadPublicKey(String resourcePath) throws Exception {
        Resource resource = resourceLoader.getResource(resourcePath);
        byte[] keyBytes = readAllBytes(resource);

        String key = new String(keyBytes)
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private byte[] readAllBytes(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return is.readAllBytes();
        }
    }

    public String createAccessToken(Authentication authentication) {
        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new RoleNotFoundException("No se encontró el rol del usuario"))
                .getAuthority();

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (jwtValidityInSeconds * 1000)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String createAccessToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (jwtValidityInSeconds * 1000)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            String role = claims.get("role", String.class);
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("El token ha expirado", e);
        } catch (JwtException e) {
            throw new RuntimeException("Token JWT inválido", e);
        }
    }
}
