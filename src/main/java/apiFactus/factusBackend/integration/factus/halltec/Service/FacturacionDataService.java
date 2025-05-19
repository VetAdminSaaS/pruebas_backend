package apiFactus.factusBackend.integration.factus.halltec.Service;

import apiFactus.factusBackend.integration.factus.halltec.Dto.municipioDTO;
import apiFactus.factusBackend.integration.factus.halltec.Dto.rangoNumericoDTO;
import apiFactus.factusBackend.integration.factus.halltec.Dto.tributoDTO;
import apiFactus.factusBackend.integration.factus.halltec.Dto.unidadesDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacturacionDataService {
    @Value("${facturacion.api.municipios.url}")
    private String facturacionApiMunicipiosUrl;
    @Value("${facturacion.api.unidades.url}")
    private String facturacionApiUnidadesUrl;
    @Value("${factus.api.rangos.url}")
    private String factusApiRangosUrl;
    @Value("${facturacion.api.tributo.url}")
    private String facturacionApiTributoUrl;
    @Value("${factus.api.paises}")
    private String facturacionApiPaisesUrl;
    private final FacturacionAuthService facturacionAuthService;
    private final RestTemplate restTemplate;

    public FacturacionDataService(FacturacionAuthService facturacionAuthService, RestTemplate restTemplate) {
        this.facturacionAuthService = facturacionAuthService;
        this.restTemplate = restTemplate;
    }

    public List<unidadesDTO> obtenerUnidades() throws JsonProcessingException {
        String url = facturacionApiUnidadesUrl;
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> rawResponse = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        System.out.println("Respuesta cruda: " + rawResponse.getBody());

        if (rawResponse.getStatusCode() == HttpStatus.OK && rawResponse.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(rawResponse.getBody());
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && dataNode.isArray()) {
                return objectMapper.readValue(dataNode.toString(), new TypeReference<List<unidadesDTO>>() {
                });

            }
        }

        throw new RuntimeException("Error al obtener unidades");
    }     HttpHeaders headers = new HttpHeaders();

    public List<municipioDTO> obtenerMunicipios() throws JsonProcessingException {
        String url = facturacionApiMunicipiosUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> rawResponse = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        System.out.println("Respuesta cruda: " + rawResponse.getBody());

        if (rawResponse.getStatusCode() == HttpStatus.OK && rawResponse.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(rawResponse.getBody());
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && dataNode.isArray()) {
                return objectMapper.readValue(dataNode.toString(), new TypeReference<List<municipioDTO>>() {});

            }
        }

        throw new RuntimeException("Error al obtener unidades");
    }


    public List<rangoNumericoDTO> obtenerRangoNumerico() {
        String url = factusApiRangosUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map<String, List<rangoNumericoDTO>>> response = restTemplate.exchange(
                url, HttpMethod.GET, request,
                new ParameterizedTypeReference<Map<String, List<rangoNumericoDTO>>>() {
                }
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<rangoNumericoDTO> rangoNumericoList = response.getBody().get("data");
            return rangoNumericoList != null ? rangoNumericoList : Collections.emptyList();
        } else {
            throw new RuntimeException("Error al obtener rangos o no hay datos disponibles");
        }
    }
    public List<tributoDTO> obtenerTributos() {
        String url = facturacionApiTributoUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );

            return Optional.ofNullable(response.getBody())
                    .map(body -> (List<Map<String, Object>>) body.get("data"))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(this::convertToTributoDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tributos: " + e.getMessage(), e);
        }
    }
    private tributoDTO convertToTributoDTO(Map<String, Object> map) {
        tributoDTO dto = new tributoDTO();
        dto.setId((Integer) map.get("id"));
        dto.setCode((String) map.get("code"));
        dto.setName((String) map.get("name"));
        return dto;
    }


}

