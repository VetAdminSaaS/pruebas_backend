package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sucursal_producto")
public class SucursalProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_sucursalproducto_producto"))
    private productos_Tienda producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sucursal_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK_sucursalproducto_sucursal"))
    private Sucursales sucursal;


    private int quantity;
}
