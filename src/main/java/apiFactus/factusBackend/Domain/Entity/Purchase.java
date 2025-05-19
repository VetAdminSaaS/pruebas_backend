package apiFactus.factusBackend.Domain.Entity;

import apiFactus.factusBackend.Domain.enums.PaymentStatus;
import apiFactus.factusBackend.Domain.enums.ShipmentStatus;
import apiFactus.factusBackend.Domain.enums.TipoEntrega;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Float total;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "shipmentStatus")
    private ShipmentStatus shipmentStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipoEntrega")
    private TipoEntrega tipoEntrega;
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<PurchaseItem> items;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id"
            , foreignKey = @ForeignKey(name = "FK_purchase_user"))
    private Usuario user;
    @Column(name = "rango_numerico_id")
    private Integer numberingRangeId;
    @Column(name = "number")
    private String number;
    @Column(name = "public_url")
    private String public_url;
    private Float discountRate;
    @OneToOne(mappedBy = "purchase", cascade = CascadeType.ALL)
    private DireccionEnvio direccionEnvio;




}
