package apiFactus.factusBackend.Dto;


import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequest {
    private Double total;
    private CustomerDTO customer;
    private List<PurchaseItemCreateUpdateDTO> items;

}
