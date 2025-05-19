package apiFactus.factusBackend.Dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class CustomerDTO {
    private String address;

    private String company = "";

    private Integer dv;

    private String email;

    private String identification;

    @JsonProperty("identification_document_id")
    private Integer identificationDocumentId;

    @JsonProperty("legal_organization_id")
    private Integer legalOrganizationId;

    @JsonProperty("municipality_id")
    private Integer municipalityId;

    private String names;

    private String phone;

    @JsonProperty("trade_name")
    private String tradeName;

    @JsonProperty("tribute_id")
    private Integer tributeId;
    private String pais;


    private boolean isPersonaJuridica() {
        return identificationDocumentId != null && identificationDocumentId == 6;
    }


    @JsonGetter("dv")
    public Integer getDv() {
        return isPersonaJuridica() ? dv : null;
    }

    @JsonGetter("company")
    public String getCompany() {
        return isPersonaJuridica() ? company : "";
    }
}
