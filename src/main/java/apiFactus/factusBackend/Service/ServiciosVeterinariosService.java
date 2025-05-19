package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.ServiciosVeterinariosDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiciosVeterinariosService {
    List<ServiciosVeterinariosDTO> getAllServiciosVeterinarios();

    Page<ServiciosVeterinariosDTO> getAllServiciosVeterinarios(Pageable pageable);

    ServiciosVeterinariosDTO findbyId(Long id);

    ServiciosVeterinariosDTO crearServicioVeterinario(ServiciosVeterinariosDTO serviciosVeterinariosDTO);

    ServiciosVeterinariosDTO updateServicioVeterinario(Long id, ServiciosVeterinariosDTO serviciosVeterinariosDTO);


    void delete(Long id);
}
