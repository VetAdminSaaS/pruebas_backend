package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.Especie;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MascotaResponseDTO {
    private Long id;
    private String nombreCompleto;
    private String raza;
    private String profilePath;
    private Especie especie;
    private LocalDate fechaNacimiento;
    private Double peso;
    private String descripcion;
    private Boolean esterilizado;
    private List<String> apoderadosNames;
    private LocalDateTime created_At;
    private LocalDateTime updated_At;
}
