package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "sucursales")
public class Sucursales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private String direccion;
    private String telefono;
    private String email;
    private String ciudad;
    private String provincia;
    private String distrito;
    @Column(columnDefinition = "TEXT")
    private String referencia;
    private LocalDateTime created_At;
    private LocalDateTime updated_At;
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SucursalProducto> sucursalProductos;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "servicio_sucursal",
            joinColumns = @JoinColumn(name = "sucursal_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<ServiciosVeterinarios> serviciosVeterinarios;


}
