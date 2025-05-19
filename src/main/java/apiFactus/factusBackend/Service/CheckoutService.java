package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.PaymentCaptureResponse;
import apiFactus.factusBackend.Dto.PaymentOrderResponse;
import apiFactus.factusBackend.Dto.PaymentStatusResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;

public interface CheckoutService {
    PaymentOrderResponse createPayment(Integer purchaseId, String returnUrl, String cancelUrl);

    PaymentCaptureResponse capturePayment(String orderId) throws MessagingException;

    @Transactional
    PaymentStatusResponse verificarEstadoCompra(Integer purchaseId) throws JsonProcessingException;
}
