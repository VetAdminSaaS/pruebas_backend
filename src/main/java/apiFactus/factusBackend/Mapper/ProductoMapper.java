package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Dto.ProductoDetailsDTO;
import apiFactus.factusBackend.Dto.productoCreateDTO;
import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    private final ModelMapper modelMapper;
    public ProductoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
    public ProductoDetailsDTO toDetailsDTO(productos_Tienda producto) {
        ProductoDetailsDTO productoDetailsDTO = modelMapper.map(producto, ProductoDetailsDTO.class);
        if (producto.getCategoria() != null) {
            productoDetailsDTO.setCategoryName(producto.getCategoria().getNombre());
        }
        return productoDetailsDTO;
    }

    public productos_Tienda toEntity(productoCreateDTO producto) {
        return modelMapper.map(producto, productos_Tienda.class);
    }
    public productoCreateDTO toDTO(productos_Tienda producto) {
        return modelMapper.map(producto, productoCreateDTO.class);
    }

}
