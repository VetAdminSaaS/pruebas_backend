package apiFactus.factusBackend.integration.factus.halltec.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class CompanyDTO {

    @JsonProperty("url_logo")
    @NotEmpty(message = "El logo no puede estar vacío")
    private String urlLogo;

    @JsonProperty("nit")
    @NotEmpty(message = "El NIT es obligatorio")
    private String nit;

    @Size(min = 1, max = 1, message = "DV debe tener un solo carácter")
    private String dv;

    @NotEmpty(message = "El nombre de la compañía es obligatorio")
    private String company;

    @NotEmpty(message = "El nombre es obligatorio")
    private String name;

    @JsonProperty("graphic_representation_name")
    private String graphicRepresentationName;

    @JsonProperty("registration_code")
    private String registrationCode;

    @JsonProperty("economic_activity")
    private String economicActivity;

    @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener 10 dígitos")
    private String phone;

    @Email(message = "Debe ser un correo válido")
    private String email;

    @JsonProperty("address")
    private String address;

    private String municipality;
    @JsonProperty("direction")
    private String direction;
}
