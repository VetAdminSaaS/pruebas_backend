package apiFactus.factusBackend.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AsistenciaResponseDTO {
    private Long id;
    private LocalDate fecha;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private String estado;
    private Long empleadoId;
    private String nombreEmpleado;
    private String nombreSucursal;

}
