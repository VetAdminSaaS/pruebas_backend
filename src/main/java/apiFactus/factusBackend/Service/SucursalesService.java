package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.SucursalesDTO;
import apiFactus.factusBackend.Dto.SucursalesDetailsDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SucursalesService {
    @Transactional
    List<SucursalesDetailsDTO> getAllSucursales();

    SucursalesDetailsDTO findById(Long id);

    SucursalesDTO create(SucursalesDTO sucursalesDTO) throws BadRequestException;

    SucursalesDTO update(Long id, SucursalesDTO sucursalesDTO);

    void delete(Long id);
}
