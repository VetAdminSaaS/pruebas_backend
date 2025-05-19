package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.EspecialidadDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EspecialidadService {
    List<EspecialidadDTO> getAllEspecialidad();

    Page<EspecialidadDTO> getAllEspecialidadDTO(Pageable pageable);

    EspecialidadDTO findById(Long id);

    EspecialidadDTO createEspecialidad(EspecialidadDTO especialidadDTO);

    EspecialidadDTO updateEspecialidad(Long id, EspecialidadDTO especialidadDTO);

    void deleteEspecialidad(Long id) throws BadRequestException;
}
