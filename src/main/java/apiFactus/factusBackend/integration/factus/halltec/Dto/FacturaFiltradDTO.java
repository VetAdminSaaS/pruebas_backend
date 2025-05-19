package apiFactus.factusBackend.integration.factus.halltec.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FacturaFiltradDTO {
    private Integer id;
    private String identification;
    private String names;
    private String email;
    private String number;
    private String prefix;
    private Float total;
    private String created_at;
    private String reference_code;
    private Integer status;


}
