package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Dto.ApoderadoAdminDTO;
import apiFactus.factusBackend.Dto.ApoderadoDTO;
import apiFactus.factusBackend.Dto.ApoderadoResponseDTO;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ApoderadoService {

    @Transactional
    ApoderadoResponseDTO crearApoderado(ApoderadoDTO apoderadoDTO);



    @Transactional
    ApoderadoResponseDTO crearApoderadoByAdmin(ApoderadoAdminDTO apoderadoadminDTO, ERole roleEnum) throws MessagingException;

    ApoderadoResponseDTO obtenerDetallesDeApoderado(Long id);

    List<ApoderadoResponseDTO> obtenerApoderados();
    @Transactional
    ApoderadoResponseDTO actualizarApoderado(Long id, ApoderadoDTO apoderadoDTO);

    @Transactional
    void eliminarApoderado(Long id);
}
