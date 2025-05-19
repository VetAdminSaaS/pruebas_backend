package apiFactus.factusBackend.Dto;

import apiFactus.factusBackend.Domain.Entity.SucursalProducto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SucursalProductoResponseDTO {
    private List<SucursalStockDTO> sucursales;
    private Long productoId;
    private String sucursalNombre;

    public SucursalProductoResponseDTO(Long productoId, List<SucursalStockDTO> sucursalesDTO) {
        this.productoId = productoId;
        this.sucursales = sucursalesDTO;
    }


}
