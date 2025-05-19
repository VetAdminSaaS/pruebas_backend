package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Customer customer;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private EmpleadoVeterinario empleadoVeterinario;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Apoderado apoderado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<ComentarioProducto> comentarios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<DireccionEnvio> direcciones;

    @Column(unique = true)
    private String verificationToken;

    private Boolean isVerified = false;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
