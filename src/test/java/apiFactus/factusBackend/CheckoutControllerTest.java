package apiFactus.factusBackend;

import apiFactus.factusBackend.Controller.CheckoutController;
import apiFactus.factusBackend.Dto.PaymentCaptureResponse;
import apiFactus.factusBackend.Dto.PaymentOrderResponse;
import apiFactus.factusBackend.Dto.PaymentStatusResponse;
import apiFactus.factusBackend.Service.CheckoutService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    private PaymentOrderResponse paymentOrderResponse;
    private PaymentCaptureResponse paymentCaptureResponse;
    private PaymentStatusResponse paymentStatusResponse;

    @BeforeEach
    void setUp() {
        paymentOrderResponse = new PaymentOrderResponse("https://paypal.com/checkout/123");
        paymentCaptureResponse = new PaymentCaptureResponse();
        paymentCaptureResponse.setCompleted(true);
        paymentCaptureResponse.setPurchaseId(1);
        paymentStatusResponse = new PaymentStatusResponse("COMPLETED");
    }

    @Test
    void createPaymentOrder_ShouldReturnCreatedStatus() throws MessagingException {
        // Arrange
        when(checkoutService.createPayment(anyInt(), anyString(), anyString()))
                .thenReturn(paymentOrderResponse);

        // Act
        ResponseEntity<PaymentOrderResponse> response = checkoutController.createPaymentOrder(
                1, "http://return.com", "http://cancel.com", "paypal");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(paymentOrderResponse, response.getBody());
    }

    @Test
    void capturePaymentOrder_WhenCompleted_ShouldReturnOkStatus() throws MessagingException {
        // Arrange
        paymentCaptureResponse.setCompleted(true);
        when(checkoutService.capturePayment(anyString()))
                .thenReturn(paymentCaptureResponse);

        // Act
        ResponseEntity<PaymentCaptureResponse> response = checkoutController.capturePaymentOrder(
                "order123", "paypal");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentCaptureResponse, response.getBody());
        assertTrue(response.getBody().isCompleted());
    }

    @Test
    void capturePaymentOrder_WhenNotCompleted_ShouldReturnBadRequestStatus() throws MessagingException {
        // Arrange
        paymentCaptureResponse.setCompleted(false);
        when(checkoutService.capturePayment(anyString()))
                .thenReturn(paymentCaptureResponse);

        // Act
        ResponseEntity<PaymentCaptureResponse> response = checkoutController.capturePaymentOrder(
                "order123", "paypal");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(paymentCaptureResponse, response.getBody());
        assertFalse(response.getBody().isCompleted());
    }

    @Test
    void getPaymentStatus_ShouldReturnOkStatus() throws MessagingException, JsonProcessingException {
        // Arrange
        when(checkoutService.verificarEstadoCompra(anyInt()))
                .thenReturn(paymentStatusResponse);

        // Act
        ResponseEntity<PaymentStatusResponse> response = checkoutController.getPaymentStatus(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentStatusResponse, response.getBody());
        assertEquals("COMPLETED", response.getBody().getStatus());
    }
}