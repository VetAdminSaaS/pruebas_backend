package apiFactus.factusBackend.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDTO {

    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @NotNull(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    @NotNull(message = "El tipo de organización es obligatorio")
    private Integer legalOrganizationId;

    @NotNull(message = "El tipo de tributo es obligatorio")
    private Integer tributeId;

    @NotBlank(message = "El documento de identificación es obligatorio")
    private String identification;

    private Integer identificationDocumentId;


    private String company;
    private String tradeName;
    private Integer dv;
    private String address;
    private String phone;
    private Integer municipalityId;
    private String pais;


    private String names;
    public boolean isIdentificationDocumentRequired() {

        return this.legalOrganizationId != null && this.legalOrganizationId == 1;
    }
}
