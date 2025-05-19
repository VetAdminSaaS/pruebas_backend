package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Domain.Entity.Purchase;
import apiFactus.factusBackend.Dto.PaymentCaptureResponse;
import apiFactus.factusBackend.Dto.PaymentOrderResponse;
import apiFactus.factusBackend.Dto.PaymentStatusResponse;
import apiFactus.factusBackend.Repository.PurchaseRepository;
import apiFactus.factusBackend.Service.CheckoutService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
@PreAuthorize("hasRole('CUSTOMER')")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final PurchaseRepository purchaseRepository;

    @PostMapping("/create")
    public ResponseEntity<PaymentOrderResponse> createPaymentOrder(
            @RequestParam Integer purchaseId,
            @RequestParam String returnUrl,
            @RequestParam String cancelUrl,
            @RequestParam(required = false, defaultValue = "paypal") String paymentProvider
    ) throws MessagingException {
        PaymentOrderResponse response = checkoutService.createPayment(purchaseId, returnUrl, cancelUrl);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/capture")
    public ResponseEntity<PaymentCaptureResponse> capturePaymentOrder(
            @RequestParam String orderId,
            @RequestParam(required = false, defaultValue = "paypal") String paymentProvider
    ) throws MessagingException {
        PaymentCaptureResponse response = checkoutService.capturePayment(orderId);

        if (response.isCompleted()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/status/{id}")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable Integer id) throws MessagingException, JsonProcessingException {
        PaymentStatusResponse status = checkoutService.verificarEstadoCompra(id);
        return ResponseEntity.ok(status);
    }


}
