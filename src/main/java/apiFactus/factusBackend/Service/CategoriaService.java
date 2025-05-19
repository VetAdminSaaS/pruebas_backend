package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.CategoriaDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoriaService {

    List<CategoriaDTO> getAll();


    CategoriaDTO findById(Integer id);


    CategoriaDTO create(CategoriaDTO categoriaDTO);


    CategoriaDTO update(Integer id, CategoriaDTO categoriaDTO);

    void delete(Integer id);
}
