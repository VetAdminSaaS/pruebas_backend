package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.SucursalProducto;
import apiFactus.factusBackend.Domain.Entity.Sucursales;
import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalProductoRepository extends JpaRepository<SucursalProducto, Long> {
    Optional<SucursalProducto> findByProductoAndSucursal(productos_Tienda producto, Sucursales sucursal);
    @Query("SELECT sp FROM SucursalProducto sp JOIN FETCH sp.sucursal WHERE sp.producto = :producto")
    List<SucursalProducto> findByProducto(@Param("producto") productos_Tienda producto);


}
