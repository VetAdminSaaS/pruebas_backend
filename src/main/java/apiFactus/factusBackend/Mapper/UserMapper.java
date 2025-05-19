package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.Customer;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    // Mapea un DTO de registro a una entidad Usuario con Customer (NaturalPerson o LegalPerson)
    public Usuario toUsuarioEntity(UserRegistrationDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());

        // Crear un Customer sin importar si es persona natural o jurídica
        Customer customer = new Customer();
        customer.setEmail(dto.getEmail());
        customer.setIdentification(dto.getIdentification());
        customer.setIdentificationDocumentId(dto.getIdentificationDocumentId());
        customer.setNames(dto.getNames()); // Si es persona natural, tendrá nombres
        customer.setTradeName(dto.getTradeName()); // Si es empresa, tendrá un nombre comercial
        customer.setCompany(dto.getCompany()); // Empresa o persona natural
        customer.setAddress(dto.getAddress());
        customer.setPhone(dto.getPhone());
        customer.setLegalOrganizationId(dto.getLegalOrganizationId());
        customer.setMunicipalityId(dto.getMunicipalityId());
        customer.setTributeId(dto.getTributeId());

        usuario.setCustomer(customer);

        return usuario;
    }

    public Usuario toUserEntity(LoginDTO loginDTO) {
        return modelMapper.map(loginDTO, Usuario.class);
    }

    public UserProfileDTO toUserProfileDTO(Usuario usuario) {
        UserProfileDTO userProfileDTO = modelMapper.map(usuario, UserProfileDTO.class);

        Customer customer = usuario.getCustomer();
        if (customer != null) {
            userProfileDTO.setEmail(customer.getEmail());
            userProfileDTO.setIdentification(customer.getIdentification());
            userProfileDTO.setIdentificationDocumentId(customer.getIdentificationDocumentId());
            userProfileDTO.setNames(customer.getNames());
            userProfileDTO.setAddress(customer.getAddress());
            userProfileDTO.setPhone(customer.getPhone());
            userProfileDTO.setLegalOrganizationId(customer.getLegalOrganizationId());
            userProfileDTO.setMunicipalityId(customer.getMunicipalityId());
            userProfileDTO.setTributeId(customer.getTributeId());
            userProfileDTO.setDv(customer.getDv());
            userProfileDTO.setTradeName(customer.getTradeName());  // Si aplica
            userProfileDTO.setCompany(customer.getCompany());      // Si aplica
            userProfileDTO.setRole(usuario.getRole().getName());


        }

        return userProfileDTO;
    }


    // Convierte un Usuario en un AuthResponse
    public static AuthResponse toAuthResponseDTO(Usuario usuario, String token) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        AuthResponse authResponse;

        // Si el usuario tiene asociado un Customer
        if (usuario.getCustomer() != null) {
            Customer customer = usuario.getCustomer();
            CustomerAuthResponse customerAuthResponse = new CustomerAuthResponse();

            // Asignar datos desde Customer
            customerAuthResponse.setId(customer.getId());
            customerAuthResponse.setIdentification(customer.getIdentification());
            customerAuthResponse.setAddress(customer.getAddress());
            customerAuthResponse.setEmail(customer.getEmail());
            customerAuthResponse.setPhone(customer.getPhone());
            customerAuthResponse.setLegalOrganizationId(customer.getLegalOrganizationId());
            customerAuthResponse.setTributeId(customer.getTributeId());
            customerAuthResponse.setMunicipalityId(customer.getMunicipalityId());
            customerAuthResponse.setDv(customer.getDv());
            customerAuthResponse.setIdentificationDocumentId(customer.getIdentificationDocumentId());
            customerAuthResponse.setNames(customer.getNames()); // Ahora `names` está en Customer

            // Si el cliente tiene datos de empresa, los asignamos
            customerAuthResponse.setCompany(customer.getCompany());
            customerAuthResponse.setTradeName(customer.getTradeName());

            authResponse = customerAuthResponse;
        } else {
            authResponse = new AuthResponse();
        }

        // Asignación del token y rol
        authResponse.setToken(token);
        authResponse.setRole(Optional.ofNullable(usuario.getRole())
                .map(role -> role.getName().toString()) // Evita NullPointerException
                .orElse("UNKNOWN"));

        return authResponse;
    }

    public UsuariosStoreDTO toUsuariosStoreDTO(Customer customer) {
        UsuariosStoreDTO dto = modelMapper.map(customer, UsuariosStoreDTO.class);

        if (customer.getUser() != null) {
            dto.setUserId(customer.getUser().getId());
        }

        return dto;
    }

}
