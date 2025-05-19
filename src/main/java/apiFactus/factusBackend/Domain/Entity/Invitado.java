package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "invitado")
public class Invitado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = true)
    private mesa mesa;
    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Role rol;
}
