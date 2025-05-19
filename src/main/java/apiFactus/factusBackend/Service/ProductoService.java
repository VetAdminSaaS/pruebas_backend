package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.ProductoDetailsDTO;
import apiFactus.factusBackend.Dto.SucursalProductoResponseDTO;
import apiFactus.factusBackend.Dto.TipoEntregaResponse;
import apiFactus.factusBackend.Dto.productoCreateDTO;
import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductoService {


    ProductoDetailsDTO createProducto(productoCreateDTO productoCreateDTO) throws BadRequestException;

    List<ProductoDetailsDTO> getAllProductos();




    ProductoDetailsDTO findById(Integer id);

    @Transactional
    Page<ProductoDetailsDTO> paginate(Pageable pageable);

    long getTotalProducts();

    @Transactional
    ProductoDetailsDTO update(Integer id, productoCreateDTO productoUpdatedto) throws BadRequestException;

    @Transactional
    void delete(Integer id);

    TipoEntregaResponse getTiposEntregaPorProducto(Long productoId);

    SucursalProductoResponseDTO getSucursalesPorProducto(Long productoId);


    List<ProductoDetailsDTO> obtenerProductosPorCategory(Integer categoryId);

    List<productos_Tienda> obtenerProductoPorNombre(String name);
}
