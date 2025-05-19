package apiFactus.factusBackend.integration.factus.halltec.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BillDto {
    private Integer id;
    private DocumentDto document;
    private String number;
    @JsonProperty("reference_code")
    private String referenceCode;
    private String status;
    @JsonProperty("send_email")
    private String sendEmail;
    private  String qr;
    private String cufe;
    private String validated;
    @JsonProperty("discount_rate")
    private BigDecimal discountRate;
    private BigDecimal discount;
    @JsonProperty("gross_value")
    private BigDecimal value;
    @JsonProperty("taxable_amount")
    private BigDecimal taxableAmount;
    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String observation;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("payment_due_date")
    private String paymentDueDate ;
    @JsonProperty("qr_image")
    private String qrImage ;
    @JsonProperty("has_claim")
    private Boolean hasClaim;
    @JsonProperty("is_negotiable_instrument")
    private Boolean isNegotiableInstrument ;
    @JsonProperty("payment_form")
    private PaymentFormDTO paymentFormDTO;
    @JsonProperty("payment_method")
    private PaymentMethodDTO paymenMethodDTO;
    private List<String> errors;
    @JsonProperty("public_url")
    private String publicUrl;
}
