package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApoderadoAdminDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    private String apellido;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;

    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(max = 20, message = "El número de identificación no puede tener más de 20 caracteres")
    private String numeroIdentificacion;

    @Size(max = 200, message = "La dirección no puede tener más de 200 caracteres")
    private String direccion;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    private String email;

    @Size(max = 15, message = "El teléfono no puede tener más de 15 caracteres")
    private String telefono;

    @Size(max = 50, message = "La provincia no puede tener más de 50 caracteres")
    private String provincia;

    @NotBlank(message = "El distrito es obligatorio")
    @Size(max = 50, message = "El distrito no puede tener más de 50 caracteres")
    private String distrito;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 50, message = "El departamento no puede tener más de 50 caracteres")
    private String departamento;

    @NotNull(message = "El género es obligatorio")
    private Genero genero;
}
