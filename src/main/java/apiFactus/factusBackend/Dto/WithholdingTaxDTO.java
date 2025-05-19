package apiFactus.factusBackend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WithholdingTaxDTO {
    private String code;
    @JsonProperty("withholding_tax_rate")
    private double withholdingTaxRate;
}
