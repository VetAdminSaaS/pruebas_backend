package apiFactus.factusBackend.Domain.Entity;

import apiFactus.factusBackend.Domain.enums.TipoEntrega;
import apiFactus.factusBackend.Dto.WithholdingTaxDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "productos")
public class productos_Tienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonProperty("code_reference")
    private String codeReference;
    private String name;
    private int quantity;
    @JsonProperty("discount_rate")
    private double discountRate;
    private double price;
    @JsonProperty("tax_rate")
    private double taxRate;
    @JsonProperty("unit_measure_id")
    private int unitMeasureId;
    @JsonProperty("standard_code_id")
    private int standardCodeId;
    @Column(name = "cover_path", columnDefinition = "TEXT")
    private String coverPath;
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "file_path")
    private String filePath;
    @Column(name = "is_excluded")
    private int isExcluded;
    @JsonProperty("tribute_id")
    private int tributeId;
    @Transient
    private List<WithholdingTaxDTO> withholdingTaxes;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id"
            ,foreignKey = @ForeignKey(name = "FK_productos_categories"))
    private Categoria categoria;
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComentarioProducto> comentarios;
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 30)
    private List<SucursalProducto> sucursalesStock;
    @ElementCollection(targetClass = TipoEntrega.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "producto_tipo_entrega", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "tipo_entrega")
    private List<TipoEntrega> tiposEntrega;

    private Double costoDespacho;
    public void setCostoDespacho(Double costoDespacho) {
        if (tiposEntrega != null && !tiposEntrega.contains(TipoEntrega.DESPACHO_A_DOMICILIO)) {
            this.costoDespacho = null;
        } else {
            this.costoDespacho = costoDespacho;
        }
    }

}
