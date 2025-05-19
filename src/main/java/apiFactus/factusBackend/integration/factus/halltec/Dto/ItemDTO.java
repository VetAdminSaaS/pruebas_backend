package apiFactus.factusBackend.integration.factus.halltec.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
    public class ItemDTO {
    @JsonProperty("code_reference")
    private String codeReference;

    private String name;
    private Integer quantity;
    private BigDecimal discount;
    @JsonProperty("discount_rate")
    private Double discountRate;
    private Double price;
    @JsonProperty("gross_value")
    private BigDecimal grossValue;
    @JsonProperty("tax_rate")
    private String taxRate;
    @JsonProperty("unit_measure_id")
    private Integer unitMeasureId;
    @JsonProperty("standard_code_id")
    private Integer standardCodeId;
    @JsonProperty("is_excluded")
    private Integer isExcluded;
    @JsonProperty("tribute_id")
    private Integer tributeId;
    @JsonProperty("withholding_taxes")
    private List<WithholdingTaxesDTO> withholdingTaxes;
    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;
    @JsonProperty("taxable_amount")
    private BigDecimal taxableAmount;
    @JsonProperty("unit_measure")
    private uniMeasureDTO unitMeasure;
    @JsonProperty("standard_code")
    private standarcodeDTO standarcodeDTO;
    private tributoDTO tribute;
    private Integer total;

}
