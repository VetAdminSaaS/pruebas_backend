package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.DireccionEnvioDTO;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface DireccionEnvioService {
    List<DireccionEnvioDTO> getAll();

    DireccionEnvioDTO findById(Long id);

    DireccionEnvioDTO create(DireccionEnvioDTO direccionEnvioDTO) ;

    DireccionEnvioDTO update(Long id, DireccionEnvioDTO direccionEnvioDTO) ;

    void delete(Long id);
}
