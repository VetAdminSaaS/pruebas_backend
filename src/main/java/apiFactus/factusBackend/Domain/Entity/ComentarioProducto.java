package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "resena_producto")
public class ComentarioProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "rating", nullable = false)
    private Integer rating;
    @Column(name = "comentario", nullable = false, columnDefinition = "TEXT")
    private String comentario;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "update_at")
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_productos_comentarios"))
    private productos_Tienda producto;
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "FK_usuarios_comentarios"))
    private Usuario usuario;
}
