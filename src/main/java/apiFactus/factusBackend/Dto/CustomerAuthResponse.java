package apiFactus.factusBackend.Dto;


import lombok.Data;

@Data
public class CustomerAuthResponse extends AuthResponse {
    private Long id;
    private String identification;
    private String address;
    private String email;
    private String phone;
    private Integer legalOrganizationId;
    private Integer tributeId;
    private Integer municipalityId;

    private String company = "";
    private String tradeName;
    private Integer dv;

    private String names;
    private Integer identificationDocumentId;

}
