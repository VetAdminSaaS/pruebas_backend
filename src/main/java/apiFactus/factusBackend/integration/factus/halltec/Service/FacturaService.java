package apiFactus.factusBackend.integration.factus.halltec.Service;

import apiFactus.factusBackend.Domain.Entity.Purchase;
import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Repository.PurchaseRepository;
import apiFactus.factusBackend.integration.factus.halltec.Dto.*;
import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacturaService {
    @Value("${factus.api.factura}")
    private String factusApiFactura;
    @Value("${factus.api.filtrar.factura}")
    private String factusApiFiltrarFactura;
    private final RestTemplate restTemplate;
    private final FacturacionAuthService facturacionAuthService;
    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);
    private final PurchaseRepository purchaseRepository;
    private static final Logger logger = LoggerFactory.getLogger(FacturaService.class);
    private final EmailService emailService;
    @Value("${spring.mail.username}")
    private String mailFrom;
    private final FacturacionActionService facturacionActionService;

    @Autowired
    public FacturaService(RestTemplate restTemplate, FacturacionAuthService facturacionAuthService, PurchaseRepository purchaseRepository, EmailService emailService, FacturacionActionService facturacionActionService) {
        this.restTemplate = restTemplate;
        this.facturacionAuthService = facturacionAuthService;
        this.purchaseRepository = purchaseRepository;
        this.emailService = emailService;
        this.facturacionActionService = facturacionActionService;
    }

    public Map<String, Object> obtenerFacturas(Integer page, Integer limit, String identification, String names, String number, String prefix, String referenceCode, String status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


        StringBuilder finalUrl = new StringBuilder(factusApiFiltrarFactura);
        finalUrl.append("?");


        if (identification != null) finalUrl.append("filter[identification]=").append(identification).append("&");
        if (names != null) finalUrl.append("filter[names]=").append(names).append("&");
        if (number != null) finalUrl.append("filter[number]=").append(number).append("&");
        if (prefix != null) finalUrl.append("filter[prefix]=").append(prefix).append("&");
        if (referenceCode != null) finalUrl.append("filter[reference_code]=").append(referenceCode).append("&");
        if (status != null) finalUrl.append("filter[status]=").append(status).append("&");
        if (page != null && limit != null) finalUrl.append("page=").append(page).append("&limit=").append(limit).append("&");


        if (finalUrl.charAt(finalUrl.length() - 1) == '&') {
            finalUrl.setLength(finalUrl.length() - 1);
        }

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    finalUrl.toString(), HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    });

            return Optional.ofNullable(response.getBody())
                    .map(body -> (Map<String, Object>) body.get("data"))
                    .map(data -> {
                        List<FacturaFiltradDTO> facturas = Optional.ofNullable((List<Map<String, Object>>) data.get("data"))
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(this::convertirAFacturaDTO)
                                .toList();
                        int total = Optional.ofNullable((Map<String, Object>) data.get("pagination"))
                                .map(p -> (Number) p.get("total"))
                                .map(Number::intValue)
                                .orElse(0);

                        return Map.of("facturas", facturas, "total", total);
                    })
                    .orElse(Collections.emptyMap());
        } catch (HttpClientErrorException e) {
            log.error("Error HTTP al obtener facturas: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            log.error("Error de conexión al obtener facturas: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al obtener facturas: {}", e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    private FacturaFiltradDTO convertirAFacturaDTO(Map<String, Object> map) {
        FacturaFiltradDTO factura = new FacturaFiltradDTO();
        factura.setId((Integer) map.get("id"));
        factura.setNumber((String) map.get("number"));
        factura.setTotal(safeGetFloat(map.get("total")));
        factura.setPrefix((String) map.get("prefix"));
        factura.setReference_code((String) map.get("reference_code"));
        factura.setStatus((Integer) map.get("status"));
        factura.setEmail((String) map.get("email"));
        factura.setNames((String) map.get("names"));
        factura.setIdentification((String) map.get("identification"));
        factura.setCreated_at((String) map.get("created_at"));

        return factura;
    }
    private Float safeGetFloat(Object value) {
        if (value instanceof Float) {
            return (Float) value;
        } else if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir a Float: " + e.getMessage());
            }
        }
        return 0.0f;
    }

    public FacturaDTO CrearFactura(FacturaDTO factura, Integer purchaseId) throws JsonProcessingException {
        String url = factusApiFactura;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (factura == null || factura.getCustomer() == null || factura.getItems() == null || factura.getItems().isEmpty()) {
            throw new IllegalArgumentException("Los datos de la factura no pueden estar vacíos o nulos");
        }
        CustomerDTO customer = factura.getCustomer();
        boolean esPersonaNatural = customer.getLegalOrganizationId() == null || "2".equals(customer.getLegalOrganizationId());
        if (esPersonaNatural) {
            customer.setNames(customer.getNames() != null ? customer.getNames().trim() : "");
            customer.setCompany("");
            log.info("Cliente identificado como persona natural.");
        } else {
            customer.setCompany(customer.getCompany() != null ? customer.getCompany().trim() : "Empresa " + customer.getIdentification());
            customer.setNames("");
            log.info("Cliente identificado como empresa.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String facturaJson = objectMapper.writeValueAsString(factura);
        log.info("Enviando factura a Factus API: {}", facturaJson);
        HttpEntity<String> request = new HttpEntity<>(facturaJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            log.info("Respuesta de Factus API: Código de estado: {}", response.getStatusCode());

            if (response.getBody() == null || response.getBody().trim().isEmpty()) {
                log.error("La respuesta de Factus API está vacía");
                throw new RuntimeException("La API de Factus devolvió una respuesta vacía.");
            }

            ApiResponseDTO<FacturaDTO> apiResponse = objectMapper.readValue(
                    response.getBody(), new TypeReference<ApiResponseDTO<FacturaDTO>>() {
                    });

            if (apiResponse.getError() != null) {
                log.error("Error al crear factura en Factus: {}", apiResponse.getMessage());
                throw new RuntimeException("Error de la API de Factus: " + apiResponse.getMessage());
            }

            FacturaDTO facturaCreada = apiResponse.getData();
            log.info("Factura creada con éxito: {}", facturaCreada);
            if (facturaCreada.getBill().getNumber() != null && purchaseId != null) {
                actualizarNumeroFacturaEnCompra(purchaseId, facturaCreada.getBill().getNumber(), facturaCreada.getBill().getPublicUrl());
            }
            sendFacturaEmail(facturaCreada);
            return facturaCreada;
        } catch (HttpClientErrorException e) {
            log.error("Error del cliente al crear factura: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error del cliente: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("Error del servidor de Factus: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error en el servidor de Factus: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error inesperado: " + e.getMessage(), e);
        }

    }

    private void sendFacturaEmail(FacturaDTO facturaCreada) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = authentication.getName();

        Map<String, Object> model = new HashMap<>();
        model.put("usuario", facturaCreada.getCustomer().getNames());
        model.put("number", facturaCreada.getBill().getNumber());
        model.put("public_url", facturaCreada.getBill().getPublicUrl());
        model.put("items", facturaCreada.getItems());
        model.put("total", facturaCreada.getBill().getTotal());

        byte[] pdfFactura = facturacionActionService.descargarFactura(facturaCreada.getBill().getNumber());

        Mail mail = emailService.createMail(
                usuarioEmail,
                "Factura " + facturaCreada.getBill().getNumber(),
                model,
                mailFrom
        );

        emailService.sendEmailWithAttachment(mail, "email/factura-confirmation-template", pdfFactura, "Factura-" + facturaCreada.getBill().getNumber() + ".pdf");
    }
    public FacturaDTO CrearFacturaManual(FacturaDTO factura) throws JsonProcessingException {
        String url = factusApiFactura;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(facturacionAuthService.obtenerToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        CustomerDTO customer = factura.getCustomer();
        boolean esPersonaNatural = customer.getLegalOrganizationId() == null || "2".equals(customer.getLegalOrganizationId());

        if (esPersonaNatural) {
            customer.setNames(customer.getNames() != null ? customer.getNames().trim() : "");
            customer.setCompany("");
            log.info("Cliente identificado como persona natural.");
        } else {
            customer.setCompany(customer.getCompany() != null ? customer.getCompany().trim() : "Empresa " + customer.getIdentification());
            customer.setNames("");
            log.info("Cliente identificado como empresa.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String facturaJson = objectMapper.writeValueAsString(factura);
        HttpEntity<String> request = new HttpEntity<>(facturaJson, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (response.getBody() == null || response.getBody().trim().isEmpty()) {
                throw new RuntimeException("La API de Factus devolvió una respuesta vacía.");
            }

            ApiResponseDTO<FacturaDTO> apiResponse = objectMapper.readValue(
                    response.getBody(), new TypeReference<ApiResponseDTO<FacturaDTO>>() {
                    });

            if (apiResponse.getError() != null) {
                throw new RuntimeException("Error de la API de Factus: " + apiResponse.getMessage());
            }
            FacturaDTO facturaCreada = apiResponse.getData();
            log.info("Factura creada con éxito: {}", facturaCreada);
            sendFacturaManualEmail(facturaCreada);
            return facturaCreada;
        } catch (HttpClientErrorException e) {
            log.error("Error del cliente al crear factura: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error del cliente: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("Error del servidor de Factus: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error en el servidor de Factus: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            throw new RuntimeException("Error inesperado: " + e.getMessage(), e);
        }
    }
    private void sendFacturaManualEmail(FacturaDTO facturaCreada) throws MessagingException {
        String usuarioEmail = facturaCreada.getCustomer().getEmail();
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> model = new HashMap<>();
        model.put("usuario", facturaCreada.getCustomer().getNames());
        model.put("number", facturaCreada.getBill().getNumber());
        model.put("public_url", facturaCreada.getBill().getPublicUrl());
        model.put("items", facturaCreada.getItems());
        model.put("total", facturaCreada.getBill().getTotal());
        byte[] pdfFactura = facturacionActionService.descargarFactura(facturaCreada.getBill().getNumber());
        Mail mail = emailService.createMail(
                usuarioEmail,
                "Factura" +facturaCreada.getBill().getNumber(),
                model,
                mailFrom
        );
        emailService.sendEmailWithAttachment(mail, "email/factura-confirmation-template", pdfFactura, "Factura-" + facturaCreada.getBill().getNumber() + ".pdf");


    }
    private void actualizarNumeroFacturaEnCompra(Integer purchaseId, String numeroFactura, String  publicUrl) {
        Optional<Purchase> optionalPurchase = purchaseRepository.findById(purchaseId);
        if (optionalPurchase.isPresent()) {
            Purchase purchase = optionalPurchase.get();
            purchase.setPublic_url(publicUrl);
            purchase.setNumber(numeroFactura);
            purchaseRepository.save(purchase);
        } else {
            throw new RuntimeException("Compra no encontrada para actualizar el número de factura.");
        }
    }
}
