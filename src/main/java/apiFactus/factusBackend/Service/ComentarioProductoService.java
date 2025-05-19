package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.ComentarioRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ComentarioProductoService {
    List<ComentarioRequestDTO> getAllComentariosPorProducto(Integer productoId);


    Page<ComentarioRequestDTO> getComentariosPorProducto(Integer productoId, Pageable pageable);

    ComentarioRequestDTO finById(Integer id);

    ComentarioRequestDTO create(ComentarioRequestDTO comentarioRequestDTO, Integer productoId);

    @Transactional
    ComentarioRequestDTO update(Integer id, ComentarioRequestDTO comentarioRequestDTO);

    @Transactional
    void delete(Integer id);

    Double obtenePromedioRatingProducto(Integer productoId);
}
