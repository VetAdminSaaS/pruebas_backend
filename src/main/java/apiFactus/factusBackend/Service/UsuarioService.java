package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Domain.Entity.Role;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Dto.AuthResponse;
import apiFactus.factusBackend.Dto.LoginDTO;
import apiFactus.factusBackend.Dto.UserProfileDTO;
import apiFactus.factusBackend.Dto.UserRegistrationDTO;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;


public interface UsuarioService {


    @Transactional
    UserProfileDTO registerCustomer(UserRegistrationDTO registrationDTO);

    Role getUserRoleByEmail(String email);

    AuthResponse login(LoginDTO loginDTO) throws MessagingException;

    long getTotalUsuarios();


    void suspendercuenta(Integer id);


    boolean reactivarCuenta(String token);

    void eliminarCuenta(Integer id) throws MessagingException;

    @Transactional
    void verificarCuenta(String token) throws BadRequestException;

    Usuario createUserWithGoogle(String email, String name);

    UserProfileDTO getUserProfileById(Integer id);

    UserProfileDTO updateUserProfile(Integer id, UserProfileDTO userProfileDTO);
}
