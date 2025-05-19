package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.ERole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserProfileDTO {
    private Integer id;
    private String email;
    private ERole role;
    private Integer identificationDocumentId;
    private String identification;
    private String address;
    private Integer municipalityId;
    private Integer tributeId;
    private String phone;
    private Integer legalOrganizationId;
    private String names;
    private String company;
    private String tradeName;
    private Integer dv;
    private String verificationToken;





}
