package apiFactus.factusBackend.Domain.Entity;

import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import apiFactus.factusBackend.Domain.enums.TipoEmpleado;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "empleado_veterinario")
public class EmpleadoVeterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "fecha_nacimiento", nullable = true)
    private LocalDate fechaNacimiento;

    @Column(name = "direccion", nullable = true)
    private String direccion;

    @Column(name = "profilePath", nullable = true, columnDefinition = "TEXT")
    private String profilePath;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "empleado_especialidad",
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidad_id"))
    private List<Especialidad> especialidades;


    @OneToMany(mappedBy = "empleadoVeterinario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EmpleadoServicio> servicios;
    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = true, referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_empleados_sucursales"))
    private Sucursales sucursal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoDocumentoIdentidad", nullable = false)
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;
    @Column(name = "numeroDocumentoIdentidad",nullable = false)
    private String numeroDocumentoIdentidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "Genero", nullable = false)
    private Genero genero;

    @Column(name = "telefono", nullable = true)
    private String telefono;

    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;

    @Column(name = "estado", nullable = false)
    private Boolean estado;
    @OneToMany(mappedBy = "empleadoVeterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asistencia> asistencias;

    @Column(name = "email")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TipoEmpleado tipoEmpleado;



    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario user;

    private LocalDateTime created_At;
    private LocalDateTime updated_At;
}
