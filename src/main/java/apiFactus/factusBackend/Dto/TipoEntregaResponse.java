package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.enums.TipoEntrega;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@RequiredArgsConstructor


public class TipoEntregaResponse {
    private Long productoId;
    private List<TipoEntrega> tiposEntrega;
    private Double costoDespacho;


    public TipoEntregaResponse(Long productoId, List<TipoEntrega> tiposEntrega, Double costoDespacho) {
        this.productoId = productoId;
        this.tiposEntrega = tiposEntrega;
        this.costoDespacho = costoDespacho;
    }
}
