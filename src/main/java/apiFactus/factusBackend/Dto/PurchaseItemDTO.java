package apiFactus.factusBackend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseItemDTO {
    private Integer id;
    @JsonProperty("code_reference")
    private String codeReference;
    private String name;
    private Integer quantity;
    @JsonProperty("discount_rate")
    private double discountRate;
    private double price;
    @JsonProperty("tax_rate")
    private double taxRate;
    @JsonProperty("unit_measure_id")
    private Integer unitMeasureId;
    private String coverPath;
    private String filePath;
    @JsonProperty("standard_code_id")
    private Integer standardCodeId;
    @JsonProperty("is_excluded")
    private Integer isExcluded;


    @JsonProperty("tribute_id")
    private Integer tributeId;
    private List<WithholdingTaxDTO> withholdingTaxes;

}
