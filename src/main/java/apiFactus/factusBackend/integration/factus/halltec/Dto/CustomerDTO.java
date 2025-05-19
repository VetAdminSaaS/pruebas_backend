package apiFactus.factusBackend.integration.factus.halltec.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {
    private String identification;
    private Integer dv;
    private String company;
    @JsonProperty("trade_name")
    private String tradeName;
    private String names;
    private String address;
    private String email;
    private String phone;
    @JsonProperty("legal_organization_id")
    private String legalOrganizationId;
    @JsonProperty("tribute_id")
    private String tributeId;
    @JsonProperty("identification_document_id")
    private String identificationDocumentId;
    @JsonProperty("municipality_id")
    private String municipalityId;
    @JsonProperty("graphic_representation_name")
    private String graphicRepresentationName;
    @JsonProperty("legal_organization")
    private LegalOrganizationDTO legalOrganization;
    @JsonProperty("tribute")
    private TributeDTO tribute;
    private PaisesDTO paisesDTO;

    private MunicipalityDTO municipality;
    public String getNamesIfNatural() {
        return (this.legalOrganizationId != null && "1".equals(this.legalOrganizationId)) ? this.names : null;
    }

}
