package apiFactus.factusBackend.integration.factus.halltec.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
@JsonPropertyOrder({
        "status",
        "message",
        "reference_code",
        "company",
        "customer",
        "observation",
        "payment_form",
        "payment_due_date",
        "payment_method_code",
        "withholding_taxes",
        "credit_notes",
        "debit_notes",
        "items",
        "numbering_range",
        "billing_period",
        "bill",
        "related_documents"
})
@Data
public class FacturaDTO {
    @JsonProperty("numbering_range_id")
    private int numberingRangeId;

    @JsonProperty("reference_code")
    private String referenceCode;

    @JsonProperty("company")
    private CompanyDTO company;

    private String observation;
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;


    @JsonProperty("withholding_taxes")
    private List<WithholdingTaxesDTO> withholdingTaxes;

    @JsonProperty("credit_notes")
    private List<String> creditNotes;

    @JsonProperty("debit_notes")
    private List<String> debitNotes;

    private CustomerDTO customer;

    private List<ItemDTO> items;

    @JsonProperty("numbering_range")
    private NumberingRangeDTO numberingRange;

    @JsonProperty("billing_period")
    @JsonDeserialize(contentAs = BillingPeriodDTO.class)
    private JsonNode billingPeriod;

    @JsonProperty("bill")
    private BillDto bill;

    @JsonProperty("related_documents")
    private List<String> relatedDocuments;

}

@Data
class PaymentMethodDTO {
    private String code;
    private String name;
}
@Data
class PaymentFormDTO {
    private String code;
    private String name;
}
@Data
class DocumentDto {
    private Integer code;
    private String name;

}

@Data
class NumberingRangeDTO {
    private String prefix;
    private String from;
    private String to;
    private String resolution_number;
    private String start_date;
    private String end_date;
    private String months;

}

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class MunicipalityDTO {
    private Integer id;
    private String name;
    private String code;
}
@Data
class TributeDTO {
    private Integer id;
    private String code;
    private String name;
}
@Data
class LegalOrganizationDTO {
    private Integer id;
    private String name;
    private String code;
}

@Data
class WithholdingTaxesDTO {
    private String name;
    private BigDecimal value;
    private List<ratesDTO> rates;
    @JsonProperty("tribute_code")
    private String tributeCode;
    private String code;
    @JsonProperty("withholding_tax_rate")
    private BigDecimal withholdingTaxRate;


}
@Data
class ratesDTO {
    private String code;
    private String name;
    private String rate;
}
@Data
class standarcodeDTO {
    private Integer id;
    private String code;
    private String name;
}
@Data
class uniMeasureDTO {
    private Integer id;
    private String code;
    private String name;
}

