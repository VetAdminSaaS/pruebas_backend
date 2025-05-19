package apiFactus.factusBackend.Dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseCreateUpadteDTO {
    private Float total;
    private CustomerDTO customer;
    private List<PurchaseItemCreateUpdateDTO> items;
    private Integer numberingRangeId;
    private String number;

}
