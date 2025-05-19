package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.Entity.DireccionEnvio;
import apiFactus.factusBackend.Domain.enums.PaymentStatus;
import apiFactus.factusBackend.Domain.enums.ShipmentStatus;
import apiFactus.factusBackend.Domain.enums.TipoEntrega;
import apiFactus.factusBackend.integration.factus.halltec.Dto.rangoNumericoDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class PurchaseDTO {
    private Integer id;
    private String public_url;
    private Float total;
    private LocalDateTime createdAt;
    private PaymentStatus paymentStatus;
    private ShipmentStatus shipmentStatus;
    private TipoEntrega tipoEntrega;
    private String names;
    private List<PurchaseItemDTO> items;
    private CustomerDTO customer;
    private Integer numberingRangeId;
    private String number;
    private String discount_rate;
    private DireccionEnvioDTO direccionEnvioDTO;
    private String coverPath;
}
