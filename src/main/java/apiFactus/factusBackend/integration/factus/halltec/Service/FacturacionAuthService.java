package apiFactus.factusBackend.integration.factus.halltec.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturacionAuthService {

    @Value("${facturacion.api.url}")
    private String facturacionApiUrl;

    @Value("${facturacion.api.auth.url}")
    private String facturacionApiAuthUrl;

    @Value("${facturacion.api.username}")
    private String facturacionApiUsername;

    @Value("${facturacion.api.password}")
    private String facturacionApiPassword;

    @Value("${factus.client-id}")
    private String facturacionClientId;

    @Value("${factus.client-secret}")
    private String facturacionClientSecret;

    private final RestTemplate restTemplate;

    private String token;
    private String refreshToken;
    private Instant tokenExpiration;

    public String obtenerToken() {
        if (token == null || tokenExpirado()) {
            autenticar();
        }
        return token;
    }

    private void autenticar() {
        String url = facturacionApiUrl + facturacionApiAuthUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("username", facturacionApiUsername);
        requestBody.add("password", facturacionApiPassword);
        requestBody.add("client_id", facturacionClientId);
        requestBody.add("client_secret", facturacionClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());

                token = jsonResponse.get("access_token").asText();
                int expiresIn = jsonResponse.get("expires_in").asInt();
                tokenExpiration = Instant.now().plusSeconds(expiresIn);

            } else {
                throw new RuntimeException("Error al autenticar: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Fallo en la autenticación: " + e.getMessage(), e);
        }
    }
    private void refrescarToken(){
        String url = facturacionApiUrl +facturacionApiAuthUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type","refresh_token");
        requestBody.add("client_id",facturacionClientId);
        requestBody.add("cliente_secret", facturacionClientSecret);
        requestBody.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                token = jsonResponse.get("refresh_token").asText();
                int expiresIn = jsonResponse.get("expires_in").asInt();
                tokenExpiration = Instant.now().plusSeconds(expiresIn);
            } else {
                throw new RuntimeException("Error al refrescar toke:" + response.getStatusCode());
            }
        } catch (Exception e){
            autenticar();
            }
        }


    private boolean tokenExpirado() {
        return tokenExpiration == null || Instant.now().isAfter(tokenExpiration);
    }
}
