package apiFactus.factusBackend.integration.factus.halltec.Service;

import apiFactus.factusBackend.integration.factus.halltec.Dto.FactusEventResponseDTO;
import apiFactus.factusBackend.integration.factus.halltec.Dto.unidadesDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FacturacionActionService {
    @Value("${factus.api.descagar.factura}")
    private String factusApiDescagarFactura;
    private final FacturacionAuthService facturacionAuthService;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(FacturaService.class);
    @Value("${factus.api.eliminar.factura}")
    private String factusApiEliminarFactura;
    @Value("${factus.api.ver.factura}")
    private String factusApiVerFactura;
    @Value("${factus.api.eventos.radianes.factura}")
    private String factusApiEventosRadianFactura;


    public FacturacionActionService(FacturacionAuthService facturacionAuthService, RestTemplate restTemplate) {
        this.facturacionAuthService = facturacionAuthService;
        this.restTemplate = restTemplate;
    }

    public byte[] descargarFactura(String numeroFactura) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    factusApiDescagarFactura + numeroFactura,
                    HttpMethod.GET,
                    new HttpEntity<>(crearHeadersAutenticacion()),
                    new ParameterizedTypeReference<>() {
                    }
            );

            return Optional.ofNullable(response.getBody())
                    .map(body -> (Map<String, Object>) body.get("data"))
                    .map(data -> data.get("pdf_base_64_encoded"))
                    .map(Object::toString)
                    .map(Base64.getDecoder()::decode)
                    .orElseGet(() -> {
                        logger.warn("No se pudo obtener la factura. Código de estado: {}", response.getStatusCode());
                        return new byte[0];
                    });

        } catch (HttpStatusCodeException e) {
            logger.error("Error en la solicitud HTTP: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error inesperado al descargar la factura", e);
        }
        return new byte[0];
    }

    private HttpHeaders crearHeadersAutenticacion() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        return headers;
    }
    public ResponseEntity<Map<String, Object>> eliminarFactura(String referenceCode) {
        String url = factusApiEliminarFactura + "/" + referenceCode;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, request, new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return ResponseEntity.ok(response.getBody());
            }
            return ResponseEntity.status(response.getStatusCode()).body(Map.of("error", "No se pudo eliminar la factura"));

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", "Error al eliminar la factura", "detalle", e.getResponseBodyAsString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado", "detalle", e.getMessage()));
        }
    }
    public String obtenerFactura(String numeroFactura, String opcion) {
        String url = factusApiVerFactura + numeroFactura;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                if (body.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) body.get("data");

                    if (data != null && data.containsKey("bill")) {
                        Map<String, Object> bill = (Map<String, Object>) data.get("bill");

                        if (bill != null) {
                            String valor = "";
                            if ("factura".equalsIgnoreCase(opcion) && bill.containsKey("public_url")) {
                                valor = bill.get("public_url").toString();
                            } else if ("dian".equalsIgnoreCase(opcion) && bill.containsKey("qr")) {
                                valor = bill.get("qr").toString();
                            }
                            valor = valor.replaceFirst("^Optional\\[(.*)]$", "$1");
                            if (!valor.isEmpty()) {
                                logger.info("Información encontrada ({}) : {}", opcion, valor);
                                return valor;
                            }
                        }
                    }
                }
            }
        } catch (HttpStatusCodeException e) {
            logger.error("Error en la solicitud HTTP: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error inesperado al obtener la información", e);
        }
        return "";
    }
  /* public FactusEventResponseDTO getFacturaEventos(String numeroFactura){
        String url = factusApiEventosRadianFactura;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));



    }*/

}
