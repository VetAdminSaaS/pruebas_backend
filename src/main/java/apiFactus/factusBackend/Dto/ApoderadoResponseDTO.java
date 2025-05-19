package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApoderadoResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;
    private String numeroIdentificacion;
    private String direccion;
    private String email;
    private String telefono;
    private String provincia;
    private String distrito;
    private String departamento;
    private Genero genero;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MascotaResponseDTO> mascotas;
}
