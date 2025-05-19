package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseItemCreateUpdateDTO {
    private Long productoId;
    private Integer quantity;
    private Double price;
    private String name;
    private Boolean isExcluded;
    @JsonProperty("code_reference")
    private String codeReference;
    @JsonProperty("unit_measure_id")
    private Long unitMeasureId;
    private String coverPath;

}
