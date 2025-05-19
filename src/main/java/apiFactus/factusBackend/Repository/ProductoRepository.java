package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.SucursalProducto;
import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import apiFactus.factusBackend.Domain.enums.TipoEntrega;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<productos_Tienda, Long> {
    @Query("SELECT COUNT(p) FROM productos_Tienda p")
    long countTotalProducts();
    @Query("SELECT p FROM productos_Tienda p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<productos_Tienda> findByNameContaining(@Param("name") String name);
    @Query("SELECT p FROM productos_Tienda p LEFT JOIN FETCH p.sucursalesStock WHERE p.id = :id")
    Optional<productos_Tienda> findByIdWithSucursalesStock(@Param("id") Long id);

    @Query("SELECT p.tiposEntrega FROM productos_Tienda p WHERE p.id = :productoId")
    List<TipoEntrega> findTiposEntregaByProductoId(Long productoId);
    @Query("SELECT p.costoDespacho FROM productos_Tienda p WHERE p.id = :productoId")
    Optional<Double> findCostoDespachoByProductoId(@Param("productoId") Long productoId);
    @Query("SELECT sp FROM SucursalProducto sp WHERE sp.producto.id = :productoId AND sp.quantity > 0")
    List<SucursalProducto> findSucursalesConStock(@Param("productoId") Long productoId);
    List<productos_Tienda> findByCategoriaId(Integer categoryId);
    @Query("SELECT p FROM productos_Tienda p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.codeReference) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<productos_Tienda> findByNameOrCodeReference(@Param("search") String search, Pageable pageable);






}
