package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.Entity.Especialidad;
import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import apiFactus.factusBackend.Domain.enums.TipoEmpleado;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class EmpleadoProfileDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String profilePath;
    private List<String> especialidades;

    private LocalDateTime created_At;
    private LocalDateTime updated_At;
    private Long sucursalId;
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;
    private String numeroDocumentoIdentidad;
    private Genero genero;
    private String telefono;
    private LocalDate fechaContratacion;
    private Boolean estado;
    private String email;
    private List<Long> serviciosIds;
    private ERole role;
    private TipoEmpleado tipoEmpleado;

}
