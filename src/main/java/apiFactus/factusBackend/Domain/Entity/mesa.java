package apiFactus.factusBackend.Domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "mesa")
public class mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer numero;
    private Integer cantidad;
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Invitado> invitados;
    public boolean puedeAgregarInvitado() {
        return invitados.size() < 8;
    }

}
