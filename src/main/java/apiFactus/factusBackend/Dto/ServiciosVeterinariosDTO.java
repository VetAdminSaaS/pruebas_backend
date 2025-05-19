package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.Entity.Sucursales;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiciosVeterinariosDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Float precio;
    private boolean disponible;
    private String coverPath;
}
