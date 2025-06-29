package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.CustomerStoreController;
import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerStoreControllerTest {

    @Mock
    private StoreService storeService;

    @InjectMocks
    private CustomerStoreController customerStoreController;

    private List<PurchaseDTO> mockPurchases;

    @BeforeEach
    void setUp() {
        PurchaseDTO purchase1 = new PurchaseDTO();
        purchase1.setId(1);
        purchase1.setTotal(100.0f);
        purchase1.setCreatedAt(LocalDateTime.now());
        purchase1.setPublic_url("https://veterinariasanfrancisco.com/invoice/1");

        PurchaseDTO purchase2 = new PurchaseDTO();
        purchase2.setId(2);
        purchase2.setTotal(150.0f);
        purchase2.setCreatedAt(LocalDateTime.now().minusDays(1));
        purchase2.setPublic_url("https://veterinariasanfrancisco.com/invoice/2");

        mockPurchases = Arrays.asList(purchase1, purchase2);
    }

    @Test
    void getLastSixPaidPurchaseByAuthenticationUser_ShouldReturnPurchases() {
        // Arrange
        when(storeService.getLastSixPaidPurchasesByAuthenticatedUser())
                .thenReturn(mockPurchases);

        // Act
        ResponseEntity<List<PurchaseDTO>> response =
                customerStoreController.getLastSixPaidPurchaseByAuthenticationUser();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPurchases, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(storeService, times(1)).getLastSixPaidPurchasesByAuthenticatedUser();
    }

    @Test
    void getLastSixPaidPurchaseByAuthenticationUser_WhenNoPurchases_ShouldReturnEmptyList() {
        // Arrange
        when(storeService.getLastSixPaidPurchasesByAuthenticatedUser())
                .thenReturn(List.of());

        // Act
        ResponseEntity<List<PurchaseDTO>> response =
                customerStoreController.getLastSixPaidPurchaseByAuthenticationUser();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());

        verify(storeService, times(1)).getLastSixPaidPurchasesByAuthenticatedUser();
    }
}
