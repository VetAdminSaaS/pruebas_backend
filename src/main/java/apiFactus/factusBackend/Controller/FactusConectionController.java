package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.exception.FacturaException;
import apiFactus.factusBackend.integration.factus.halltec.Dto.*;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturaService;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturacionActionService;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturacionDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/factus")
@RequiredArgsConstructor
public class FactusConectionController {

    private final FacturaService facturaService;
    private final FacturacionDataService facturacionDataService;
    private final FacturacionActionService facturacionActionService;
    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);

    @GetMapping("/rango-numerico")
    public ResponseEntity<List<rangoNumericoDTO>> obtenerRangoNumerico() {
        List<rangoNumericoDTO> rangos = facturacionDataService.obtenerRangoNumerico();
        return ResponseEntity.ok(rangos);
    }
    @GetMapping("/unidades-medida")
    public ResponseEntity<List<unidadesDTO>> obtenerUnidadesMedida() throws JsonProcessingException {
        List<unidadesDTO> unidades = facturacionDataService.obtenerUnidades();
        return ResponseEntity.ok(unidades);
    }
    @GetMapping("/municipios")
    public ResponseEntity<List<municipioDTO>> obtenerMunicipios() throws JsonProcessingException {
        List<municipioDTO> municipios = facturacionDataService.obtenerMunicipios();
        return ResponseEntity.ok(municipios);

    }
    @GetMapping("/tributos")
    public ResponseEntity<List<tributoDTO>> obtenerTributos() throws JsonProcessingException {
        List<tributoDTO> tributos = facturacionDataService.obtenerTributos();
        return ResponseEntity.ok(tributos);
    }
    @GetMapping("/obtener/facturas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerFacturaFiltrad(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String identification,
            @RequestParam(required = false) String names,
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String prefix,
            @RequestParam(required = false) String referenceCode,
            @RequestParam(required = false) String status) {
        try {
            log.info(" Recibiendo solicitud para obtener facturas. Página: {}, Límite: {}, Filtros: [identification: {}, names: {}, number: {}, prefix: {}, referenceCode: {}, status: {}]",
                    page, limit, identification, names, number, prefix, referenceCode, status);

            Map<String, Object> resultado = facturaService.obtenerFacturas(page, limit, identification, names, number, prefix, referenceCode, status);

            if (resultado.isEmpty() || ((List<?>) resultado.get("facturas")).isEmpty()) {
                log.info("ℹNo se encontraron facturas.");
                return ResponseEntity.noContent().build();
            }

            log.info(" Facturas obtenidas correctamente. Total: {}", resultado.get("total"));

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener facturas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/crearFactura/{purchaseId}")
    public ResponseEntity<?> crearFactura(@PathVariable Integer purchaseId, @RequestBody FacturaDTO facturaDTO) {
        try {

            FacturaDTO facturaCreada = facturaService.CrearFactura(facturaDTO, purchaseId);


            return ResponseEntity.ok(facturaCreada);
        } catch (JsonProcessingException e) {
            log.error("Error al procesar la factura: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error al procesar la factura", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error al crear la factura: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al crear la factura", e.getMessage()));
        }
    }
    @PostMapping("/crearFacturaManual")
    public ResponseEntity<?> crearFacturaManual(@RequestBody FacturaDTO facturaDTO) {
        try {
            FacturaDTO facturaCreada = facturaService.CrearFacturaManual(facturaDTO);
            return ResponseEntity.ok(facturaCreada);
        } catch (JsonProcessingException e) {
            log.error("Error al procesar la factura: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error al procesar la factura", e.getMessage()));
        } catch (FacturaException e) {
            log.error("Error específico al crear la factura: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ErrorResponse("Error en la lógica de negocio", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error inesperado al crear la factura: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno al crear la factura", e.getMessage()));
        }
    }



    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
    @GetMapping("/verFactura/{number}")
    public ResponseEntity<?> verFactura(@PathVariable String number) {
        String publicUrl = facturacionActionService.obtenerFactura(number, "factura");
        String qr = facturacionActionService.obtenerFactura(number, "dian");

        if (!publicUrl.isEmpty() || !qr.isEmpty()) {

            return ResponseEntity.ok(Map.of(
                    "data", Map.of(
                            "bill", Map.of(
                                    "public_url", publicUrl,
                                    "qr", qr
                            )
                    )
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No se pudo obtener la factura."));
        }
    }
    @GetMapping("/download-pdf/{number}")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable String number) {
        log.info("Solicitando descarga de factura: {}", number);

        byte[] pdfBytes = facturacionActionService.descargarFactura(number);

        if (pdfBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

                HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("factura_" + number + ".pdf")
                .build());

        log.info("Enviando factura como PDF...");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    @DeleteMapping("/{reference_code}")
    public ResponseEntity<Map<String, Object>> eliminarFactura(@PathVariable String reference_code) {
        return facturacionActionService.eliminarFactura(reference_code);
    }





}
