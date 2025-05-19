package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reactivation_tokens")
@Data
public class reactivationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column
    private LocalDateTime usedAt;

    public boolean isExpired() {
        return createdAt.plusMinutes(5).isBefore(LocalDateTime.now());
    }
    public void marcarComoUsado() {
        this.usedAt = LocalDateTime.now();
    }
    public boolean haExpirado() {
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(this.createdAt.plusMinutes(5));
    }

}
