package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.enums.ShipmentStatus;
import apiFactus.factusBackend.Domain.enums.TipoEntrega;
import apiFactus.factusBackend.Dto.PaymentCaptureResponse;
import apiFactus.factusBackend.Dto.PaymentOrderResponse;
import apiFactus.factusBackend.Dto.PaymentStatusResponse;
import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Service.CheckoutService;
import apiFactus.factusBackend.Service.PurchaseService;
import apiFactus.factusBackend.integration.factus.halltec.Dto.FacturaDTO;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturaService;
import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import apiFactus.factusBackend.integration.payment.paypal.dto.OrderCaptureResponse;
import apiFactus.factusBackend.integration.payment.paypal.dto.OrderResponse;
import apiFactus.factusBackend.integration.payment.paypal.service.PaypalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CheckoutServiceImpl implements CheckoutService {
    private final PaypalService paypalService;
    private final PurchaseService purchaseService;
    private final EmailService emailService;
    private final FacturaService facturaService;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Transactional
    @Override
    public PaymentOrderResponse createPayment(Integer purchaseId, String returnUrl, String cancelUrl) {
        OrderResponse orderResponse = paypalService.createOrder(purchaseId, returnUrl, cancelUrl);

        String paypalUrl = orderResponse
                .getLinks()
                .stream()
                .filter(link -> link.getRel().equals("approve"))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getHref();
        return new PaymentOrderResponse(paypalUrl);
    }

    @Override
    public PaymentCaptureResponse capturePayment(String orderId) throws MessagingException {
        OrderCaptureResponse orderCaptureResponse = paypalService.captureOrder(orderId);
        boolean completed = orderCaptureResponse.getStatus().equals("COMPLETED");

        PaymentCaptureResponse paymentCaptureResponse = new PaymentCaptureResponse();
        paymentCaptureResponse.setCompleted(completed);

        if (completed) {
            String purchaseIdStr = orderCaptureResponse.getPurchaseUnits().get(0).getReferenceId();
            int purchaseId = Integer.parseInt(purchaseIdStr);

            PurchaseDTO purchaseDTO = purchaseService.confirmPurchase(purchaseId);
            if(purchaseDTO.getTipoEntrega() == TipoEntrega.RETIRO_EN_TIENDA){
                purchaseDTO = purchaseService.updateShipmentStatus(purchaseId, ShipmentStatus.COMPRA_RECIBIDA);
            } else {
                purchaseDTO = purchaseService.updateShipmentStatus(purchaseId, ShipmentStatus.COMPRA_RECIBIDA);
            }


            paymentCaptureResponse.setPurchaseId(purchaseDTO.getId());

            sendPurchaseConfirmationEmail(purchaseDTO);
        }
        return paymentCaptureResponse;
    }


    private void sendPurchaseConfirmationEmail(PurchaseDTO purchaseDTO) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioemail = authentication.getName();

        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuarioemail);
        System.out.println("URL pública: " + purchaseDTO.getPublic_url());
        model.put("nombre", purchaseDTO.getNames());
        model.put("number", purchaseDTO.getPublic_url());

        model.put("total", purchaseDTO.getTotal());
        model.put("items", purchaseDTO.getItems());
        model.put("estado", purchaseDTO.getShipmentStatus());
        model.put("orderUrl", "http://localhost:4200/order/" + purchaseDTO.getId());

        if(purchaseDTO.getTipoEntrega() == TipoEntrega.RETIRO_EN_TIENDA){
            model.put("entrega", "Retiro en Tienda");
        } else if (
            purchaseDTO.getTipoEntrega() == TipoEntrega.DESPACHO_A_DOMICILIO){
            model.put("entrega", "Despacho A Domicilio");
            model.put("direccionEnvio", purchaseDTO.getDireccionEnvioDTO().getDireccion());

        }

        Mail mail = emailService.createMail(
                usuarioemail,
                "Confirmación de Compra",
                model,
                mailFrom
        );
        emailService.sendEmail(mail, "email/purchase-confirmation-template");

    }

    @Transactional
    @Override
    public PaymentStatusResponse verificarEstadoCompra(Integer id) {
        PurchaseDTO purchaseDTO = purchaseService.getPurchaseById(id);
        return new PaymentStatusResponse(purchaseDTO.getPaymentStatus().name());
    }

}
