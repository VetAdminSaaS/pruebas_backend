package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.FactusConectionController;
import apiFactus.factusBackend.integration.factus.halltec.Dto.*;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturaService;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturacionActionService;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturacionDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class facturacionControllerTest {

    @Mock
    private FacturaService facturaService;

    @Mock
    private FacturacionDataService facturacionDataService;

    @Mock
    private FacturacionActionService facturacionActionService;

    @InjectMocks
    private FactusConectionController controller;

    private rangoNumericoDTO rangoNumericoDTO;
    private unidadesDTO unidadDTO;
    private municipioDTO municipioDTO;
    private tributoDTO tributoDTO;
    private FacturaDTO facturaDTO;

    @BeforeEach
    void setUp() {
        rangoNumericoDTO = new rangoNumericoDTO();
        unidadDTO = new unidadesDTO();
        municipioDTO = new municipioDTO();
        tributoDTO = new tributoDTO();
        facturaDTO = new FacturaDTO();
    }

    @Test
    void obtenerRangoNumerico_ShouldReturnList() {
        when(facturacionDataService.obtenerRangoNumerico()).thenReturn(List.of(rangoNumericoDTO));

        ResponseEntity<List<rangoNumericoDTO>> response = controller.obtenerRangoNumerico();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void obtenerUnidadesMedida_ShouldReturnList() throws JsonProcessingException {
        when(facturacionDataService.obtenerUnidades()).thenReturn(List.of(unidadDTO));

        ResponseEntity<List<unidadesDTO>> response = controller.obtenerUnidadesMedida();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void obtenerMunicipios_ShouldReturnList() throws JsonProcessingException {
        when(facturacionDataService.obtenerMunicipios()).thenReturn(List.of(municipioDTO));

        ResponseEntity<List<municipioDTO>> response = controller.obtenerMunicipios();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void obtenerTributos_ShouldReturnList() throws JsonProcessingException {
        when(facturacionDataService.obtenerTributos()).thenReturn(List.of(tributoDTO));

        ResponseEntity<List<tributoDTO>> response = controller.obtenerTributos();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void verFactura_ShouldReturnPublicUrlAndQR() {
        when(facturacionActionService.obtenerFactura("123", "factura")).thenReturn("https://url/factura");
        when(facturacionActionService.obtenerFactura("123", "dian")).thenReturn("QR_CODE_DATA");

        ResponseEntity<?> response = controller.verFactura("123");

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(((Map<?, ?>) ((Map<?, ?>) body.get("data")).get("bill")).containsKey("public_url"));
    }

    @Test
    void verFactura_ShouldReturnNotFound_WhenEmpty() {
        when(facturacionActionService.obtenerFactura("123", "factura")).thenReturn("");
        when(facturacionActionService.obtenerFactura("123", "dian")).thenReturn("");

        ResponseEntity<?> response = controller.verFactura("123");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void eliminarFactura_ShouldReturnResponse() {
        Map<String, Object> responseMock = Map.of("message", "Eliminado correctamente");

        when(facturacionActionService.eliminarFactura("ABC123"))
                .thenReturn(ResponseEntity.ok(responseMock));

        ResponseEntity<Map<String, Object>> response = controller.eliminarFactura("ABC123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Eliminado correctamente", response.getBody().get("message"));
    }
}
