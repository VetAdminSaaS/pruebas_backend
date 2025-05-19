package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.Especie;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MascotaRequestDTO {
    private String nombreCompleto;
    private String raza;
    private String profilePath;
    private Especie especie;
    private LocalDate fechaNacimiento;
    private Double peso;
    private String descripcion;
    private Boolean esterilizado;
    private List<Long> apoderadoIds;

}
