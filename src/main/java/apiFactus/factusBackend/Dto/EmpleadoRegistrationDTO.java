package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import apiFactus.factusBackend.Domain.enums.TipoEmpleado;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmpleadoRegistrationDTO {
    @Email(message = "El correo electrónico no es válido")
    private String email;

    @NotNull(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String nombre;
    private String apellido;

    private List<Long> especialidadIds;

    private TipoDocumentoIdentidad tipoDocumentoIdentidad;
    private String numeroDocumentoIdentidad;
    private Genero genero;
    private LocalDate fechaContratacion;
    private Boolean estado;
    private Long sucursalId;
    private List<Long> serviciosIds;
    private TipoEmpleado tipoEmpleado;

}

