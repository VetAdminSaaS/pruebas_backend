package apiFactus.factusBackend.Domain.Entity;

import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "apoderado")
public class Apoderado {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", nullable = false)
  private String nombre;

  @Column(name = "apellido", nullable = false)
  private String apellido;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_documento_identidad", nullable = false)
  private TipoDocumentoIdentidad tipoDocumentoIdentidad;

  @Column(nullable = false, unique = true, name = "numero_identificacion")
  private String numeroIdentificacion;

  @Column(nullable = false, name = "direccion")
  private String Direccion;

  @Column(nullable = false)
  @Email
  private String email;

  @Column(nullable = true, name = "telefono")
  private String telefono;

  @Column(nullable = true, name = "provincia")
  private String provincia;

  @Column(nullable = false, name = "distrito")
  private String distrito;

  @Column(name = "departamento", nullable = false)
  private String departamento;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private Usuario user;

  @Enumerated(EnumType.STRING)
  @Column(name = "genero", nullable = false)
  private Genero genero;

  @ManyToMany(mappedBy = "apoderados", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JsonIgnore
  private List<Mascota> mascotas;

  private LocalDateTime created_At;
  private LocalDateTime updated_At;
  private LocalDate fechaNacimiento;

  @PrePersist
  protected void onCreate() {
    created_At = LocalDateTime.now();
    updated_At = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updated_At = LocalDateTime.now();
  }
}
