package apiFactus.factusBackend.Dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UsuariosStoreDTO {
    private Integer id;
    private String identification;
    private String names;
    private String address;
    private String email;
    private String phone;
    private Integer legalOrganizationId;
    private Integer tributeId;
    private Integer municipalityId;
    private String company;
    private String tradeName;
    private Integer dv;
    private Integer identificationDocumentId;
    private Integer userId;

}
