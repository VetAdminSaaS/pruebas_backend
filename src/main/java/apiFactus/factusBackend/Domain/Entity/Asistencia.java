package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asistencias")
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    @Column(name = "hora_entrada")
    private LocalDateTime horaEntrada;
    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoVeterinario empleadoVeterinario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursales sucursal;
    private LocalDateTime created_At;
    private LocalDateTime updated_At;

}
