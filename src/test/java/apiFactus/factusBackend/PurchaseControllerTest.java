package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.PurchaseController;
import apiFactus.factusBackend.Dto.*;
import apiFactus.factusBackend.Service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    private PurchaseDTO samplePurchase;

    @BeforeEach
    void setUp() {
        samplePurchase = new PurchaseDTO();
        samplePurchase.setId(1);
        samplePurchase.setTotal(100.0f);
        samplePurchase.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void crearCompra_ShouldReturnCreatedPurchase() {
        // Arrange
        PurchaseCreateUpadteDTO createDTO = new PurchaseCreateUpadteDTO();
        createDTO.setTotal(100.0f);
        when(purchaseService.createPurchase(createDTO)).thenReturn(samplePurchase);

        // Act
        ResponseEntity<PurchaseDTO> response = purchaseController.crearCompra(createDTO);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(samplePurchase, response.getBody());
        verify(purchaseService, times(1)).createPurchase(createDTO);
    }

    @Test
    void confirmPurchase_ShouldReturnConfirmedPurchase() {
        // Arrange
        when(purchaseService.confirmPurchase(1)).thenReturn(samplePurchase);

        // Act
        ResponseEntity<PurchaseDTO> response = purchaseController.confirmPurchase(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(samplePurchase, response.getBody());
        verify(purchaseService).confirmPurchase(1);
    }

    @Test
    void getPurchaseStatus_WhenFound_ShouldReturnPurchase() {
        // Arrange
        when(purchaseService.getPurchaseById(1)).thenReturn(samplePurchase);

        // Act
        ResponseEntity<PurchaseDTO> response = purchaseController.getPurchaseStatus(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(samplePurchase, response.getBody());
        verify(purchaseService).getPurchaseById(1);
    }

    @Test
    void getPurchaseStatus_WhenNotFound_ShouldReturnNotFound() {
        // Arrange
        when(purchaseService.getPurchaseById(99)).thenReturn(null);

        // Act
        ResponseEntity<PurchaseDTO> response = purchaseController.getPurchaseStatus(99);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(purchaseService).getPurchaseById(99);
    }

    @Test
    void getTotalPaidPurchases_ShouldReturnTotal() {
        // Arrange
        when(purchaseService.getTotalPaidPurchases()).thenReturn(500.0);

        // Act
        ResponseEntity<Double> response = purchaseController.getTotalPaidPurchases();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(500.0, response.getBody());
        verify(purchaseService).getTotalPaidPurchases();
    }

    @Test
    void getPurchaseById_ShouldReturnPurchase() {
        // Arrange
        when(purchaseService.getPurchaseById(1)).thenReturn(samplePurchase);

        // Act
        ResponseEntity<PurchaseDTO> response = purchaseController.getPurchaseById(1);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(samplePurchase, response.getBody());
    }

    @Test
    void getPurchaseByUserId_ShouldReturnListOfPurchases() {
        // Arrange
        List<PurchaseDTO> mockList = List.of(samplePurchase);
        when(purchaseService.getPurchaseHistoryByUserId()).thenReturn(mockList);

        // Act
        ResponseEntity<List<PurchaseDTO>> response = purchaseController.getPurchaseById();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void contarCompras_ShouldReturnCount() {
        // Arrange
        when(purchaseService.contarComprasConNumero()).thenReturn(10L);

        // Act
        ResponseEntity<Long> response = purchaseController.contarCompras();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10L, response.getBody());
    }

    @Test
    void obtenerVentasDelMes_ShouldReturnSalesMap() {
        // Arrange
        Map<String, Double> ventas = Map.of("actual", 2000.0, "anterior", 1500.0);
        when(purchaseService.obtenerVentasMesActualYAnterior()).thenReturn(ventas);

        // Act
        ResponseEntity<Map<String, Double>> response = purchaseController.obtenerVentasDelMes();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ventas, response.getBody());
    }

    @Test
    void obtenerComparacionVentas_ShouldReturnComparisonMap() {
        // Arrange
        Map<String, Object> comparacion = Map.of("incremento", 20.0);
        when(purchaseService.obtenerComparacionVentas()).thenReturn(comparacion);

        // Act
        ResponseEntity<Map<String, Object>> response = purchaseController.obtenerComparacionVentas();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(comparacion, response.getBody());
    }
}
