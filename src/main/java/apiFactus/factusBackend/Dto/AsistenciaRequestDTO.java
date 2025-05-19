package apiFactus.factusBackend.Dto;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data

public class AsistenciaRequestDTO {
    private Long empleadoId;
    private Long sucursalId;
    private boolean marcarEntrada;
    private LocalDateTime horaManual;

}
