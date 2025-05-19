package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "password_reset_token")
public class recuperarContrasenaToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 32, max = 256)
    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiration;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public recuperarContrasenaToken(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        this.expiration = LocalDateTime.now().plusMinutes(10);
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }
}
