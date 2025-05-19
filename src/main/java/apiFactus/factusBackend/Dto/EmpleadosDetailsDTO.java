package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import apiFactus.factusBackend.Domain.enums.TipoEmpleado;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmpleadosDetailsDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String profilePath;
    private List<String> especialidadesNombres;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long sucursalId;
    private List<Long> especialidadIds;
    private String sucursalName;
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;
    private String numeroDocumentoIdentidad;
    private Genero genero;
    private String telefono;
    private LocalDate fechaContratacion;
    private Boolean estado;
    private String email;
    private List<String> nombreServicio;
    private List<Long> serviciosIds;
    private TipoEmpleado tipoEmpleado;


}
