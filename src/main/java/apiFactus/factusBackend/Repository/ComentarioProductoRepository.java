package apiFactus.factusBackend.Repository;

import apiFactus.factusBackend.Domain.Entity.ComentarioProducto;
import apiFactus.factusBackend.Service.ComentarioProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioProductoRepository extends JpaRepository<ComentarioProducto, Integer> {
    List<ComentarioProducto> findByProductoId(Integer productoId);

    Page<ComentarioProducto> findByProductoId(Integer productoId, Pageable pageable);
    @Query("SELECT AVG(c.rating) FROM ComentarioProducto c WHERE c.producto.id = :productoId")
    Double obtenerPromedioPorProducto(@Param("productoId") Long productoId);
    boolean existsByProductoIdAndUsuarioId(Integer productoId, Long usuarioId);

}
