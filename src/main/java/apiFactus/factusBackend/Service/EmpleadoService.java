package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.EmpleadoProfileDTO;
import apiFactus.factusBackend.Dto.EmpleadoRegistrationDTO;
import apiFactus.factusBackend.Dto.EmpleadosDTO;
import apiFactus.factusBackend.Dto.EmpleadosDetailsDTO;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmpleadoService {
    List<EmpleadosDetailsDTO> getAll();

    EmpleadosDetailsDTO findById(Long id);
    @Transactional
    EmpleadoRegistrationDTO crearEmpleado(EmpleadoRegistrationDTO empleadosDTO) throws BadRequestException, MessagingException;

    EmpleadosDetailsDTO update(Long id, EmpleadosDetailsDTO empleadoUpdate);

    void delete(Long id);

    EmpleadoProfileDTO registroEmpleadoVeterinario(EmpleadoRegistrationDTO empleadoRegistrationDTO) throws MessagingException, BadRequestException;

    EmpleadosDTO completarRegistro(Long id, EmpleadosDTO empleadoDTO);
}
