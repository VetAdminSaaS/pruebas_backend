package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "direccionEnvio")
public class DireccionEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String direccion;
    private String ciudad;
    private String provincia;
    private String distrito;
    private String codigoPostal;
    private Integer piso;
    private String telefono;
    private String referencia;
    private String nombre;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @OneToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
