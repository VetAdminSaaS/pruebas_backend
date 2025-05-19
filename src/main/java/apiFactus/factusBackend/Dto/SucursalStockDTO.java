package apiFactus.factusBackend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SucursalStockDTO {
    private Long sucursalId;
    private Integer quantity;


    public SucursalStockDTO(Long sucursalId, String nombre, int quantity) {
        this.sucursalId = sucursalId;
        this.quantity = quantity;

    }
}
