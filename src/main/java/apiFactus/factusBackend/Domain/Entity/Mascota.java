package apiFactus.factusBackend.Domain.Entity;

import apiFactus.factusBackend.Domain.enums.Especie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "mascota")
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String raza;

    @Column(name = "profile_path", nullable = true, columnDefinition = "TEXT")
    private String profilePath;

    @Enumerated(EnumType.STRING)
    private Especie especie;

    private LocalDate fechaNacimiento;

    @Column(name = "peso", nullable = true)
    private Double peso;

    @Column(name = "descripcion", nullable = true)
    private String descripcion;

    @Column(name = "esterilizado", nullable = false)
    private Boolean esterilizado;

    @ManyToMany
    @JoinTable(
            name = "mascota_apoderado",
            joinColumns = @JoinColumn(name = "mascota_id"),
            inverseJoinColumns = @JoinColumn(name = "apoderado_id")
    )
    private List<Apoderado> apoderados;


    private LocalDateTime created_At;
    private LocalDateTime updated_At;

    @PrePersist
    protected void onCreate() {
        created_At = LocalDateTime.now();
        updated_At = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated_At = LocalDateTime.now();
    }
}
