package apiFactus.factusBackend.Domain.Entity;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, name = "identification_document_id")
    private Integer identificationDocumentId;

    @Column(nullable = false, unique = true)
    private String identification;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = false)
    private Integer legalOrganizationId;

    @Column(nullable = false)
    private Integer tributeId;
    @Column(nullable = true)
    private Integer dv;
    @Column(nullable = true)
    private String tradeName;

    @Column(nullable = true)
    private Integer municipalityId;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Usuario user;
    @Column(nullable = false, name = "names")
    private String names;
    @Column(nullable = true,name="pais" )
    private String pais;

    @Column(nullable = true)
    private String company;
}
