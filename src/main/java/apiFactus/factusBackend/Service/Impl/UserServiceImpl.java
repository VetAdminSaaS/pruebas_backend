package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.*;
import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Dto.AuthResponse;
import apiFactus.factusBackend.Dto.LoginDTO;
import apiFactus.factusBackend.Dto.UserProfileDTO;
import apiFactus.factusBackend.Dto.UserRegistrationDTO;
import apiFactus.factusBackend.Mapper.UserMapper;
import apiFactus.factusBackend.Repository.CustomerRepository;
import apiFactus.factusBackend.Repository.ReactivationTokenRepository;
import apiFactus.factusBackend.Repository.RoleRepository;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Security.TokenProvider;
import apiFactus.factusBackend.Service.TokenReactivationService;
import apiFactus.factusBackend.Service.UsuarioService;
import apiFactus.factusBackend.exception.InvalidCredentialsException;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static apiFactus.factusBackend.Domain.enums.ERole.CUSTOMER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final ReactivationTokenRepository reactivationTokenRepository;
    @Value("${spring.mail.username}")
    private String mailFrom;
    private final TokenReactivationService tokenReactivationService;


    @Transactional
    @Override
    public UserProfileDTO registerCustomer(UserRegistrationDTO registrationDTO) {
        return registerUser(registrationDTO, CUSTOMER);
    }
    private UserProfileDTO registerUser(UserRegistrationDTO registrationDTO, ERole roleEnum) {
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        registrationDTO.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        // Verificar si es persona natural y el campo identificationDocumentId no sea nulo
        if (registrationDTO.getLegalOrganizationId() == 1 && registrationDTO.getIdentificationDocumentId() == null) {
            throw new IllegalArgumentException("El documento de tipo de identificación es obligatorio para personas naturales");
        }

        // Si es persona natural y se identifica con NIT, verificar el dígito de verificación (DV)
        if (registrationDTO.getLegalOrganizationId() == 1 &&
                registrationDTO.getIdentification().startsWith("NIT") &&
                registrationDTO.getDv() == null) {
            throw new IllegalArgumentException("El Dígito de Verificación (DV) es obligatorio para clientes con NIT.");
        }

        Usuario usuario = userMapper.toUsuarioEntity(registrationDTO);
        usuario.setRole(role);

        // Verificar el tipo de persona (Natural o Jurídica)
        if (registrationDTO.getLegalOrganizationId() == 2) {
            // Persona Natural
            Customer naturalPerson = new Customer();
            naturalPerson.setIdentification(registrationDTO.getIdentification());
            naturalPerson.setNames(registrationDTO.getNames());
            naturalPerson.setAddress(registrationDTO.getAddress());
            naturalPerson.setEmail(registrationDTO.getEmail());
            naturalPerson.setPhone(registrationDTO.getPhone());
            naturalPerson.setLegalOrganizationId(registrationDTO.getLegalOrganizationId());
            naturalPerson.setTributeId(registrationDTO.getTributeId());
            naturalPerson.setMunicipalityId(registrationDTO.getMunicipalityId());
            naturalPerson.setIdentificationDocumentId(registrationDTO.getIdentificationDocumentId()); // Solo para personas naturales
            naturalPerson.setDv(registrationDTO.getDv());
            naturalPerson.setPais(registrationDTO.getPais());

            // Asociar la entidad Usuario con la entidad NaturalPerson
            usuario.setCustomer(naturalPerson);
        } else if (registrationDTO.getLegalOrganizationId() == 1) {
            // Persona Jurídica
          Customer legalPerson = new Customer();
            legalPerson.setIdentification(registrationDTO.getIdentification());
            legalPerson.setCompany(registrationDTO.getCompany());
            legalPerson.setTradeName(registrationDTO.getTradeName());
            legalPerson.setAddress(registrationDTO.getAddress());
            legalPerson.setEmail(registrationDTO.getEmail());
            legalPerson.setPhone(registrationDTO.getPhone());
            legalPerson.setLegalOrganizationId(registrationDTO.getLegalOrganizationId());
            legalPerson.setTributeId(registrationDTO.getTributeId());
            legalPerson.setMunicipalityId(registrationDTO.getMunicipalityId());
            legalPerson.setPais(registrationDTO.getPais());

            // Asociar la entidad Usuario con la entidad LegalPerson
            usuario.setCustomer(legalPerson);
        } else {
            throw new IllegalArgumentException("Tipo de organización no válido.");
        }

        // Guardar el customer asociado
        if (usuario.getCustomer() != null) {
            if (usuario.getCustomer().getId() == null) {
                customerRepository.save(usuario.getCustomer());
            }
            usuario.getCustomer().setUser(usuario); // Establecer la relación correctamente
        }

        Usuario usuarioSaved = usuarioRepository.save(usuario);
        return userMapper.toUserProfileDTO(usuarioSaved);
    }
    @Override
    public Usuario createUserWithGoogle(String email, String name) {
        if (usuarioRepository.existsByEmail(email)) {
            return null;
        }

        Role customerRole = roleRepository.findByName(ERole.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Error: Rol CUSTOMER no encontrado")); // Manejo de error si el rol no existe

        Usuario newUser = new Usuario();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode("google_oauth")); // Dummy password
        newUser.setRole(customerRole); // ✅ Asigna la entidad Role, no el enum

        usuarioRepository.save(newUser);
        return newUser;
    }
    @Override
    public Role getUserRoleByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        return usuario.getRole(); // ✅ Devuelve el rol del usuario
    }






    @Override
    public AuthResponse login(LoginDTO loginDTO) throws MessagingException {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Credenciales incorrectas");
        }

        if (!usuario.isActivo()) {
            reactivationToken reactivationToken = tokenReactivationService.generarToken(usuario);

            enviarEmailReactivacion(usuario, reactivationToken.getToken());

            throw new DisabledException("La cuenta está suspendida. Se ha enviado un correo para la reactivación.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        String token = tokenProvider.createAccessToken(authentication);
        return UserMapper.toAuthResponseDTO(usuario, token);
    }

    @Override
    public long getTotalUsuarios() {
        return usuarioRepository.count();
    }

    @Override
    public void suspendercuenta(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe"));
        usuario.setActivo(false); // Suspender cuenta
        usuarioRepository.save(usuario);
    }

    @Override
    public boolean reactivarCuenta(String token) {
        // Buscar el token de reactivación
        reactivationToken reactivationToken = reactivationTokenRepository.findByToken(token)
                .orElse(null);

        // Verificar si el token existe y no ha expirado (5 minutos)
        if (reactivationToken == null || reactivationToken.haExpirado()) {
            if (reactivationToken != null) {
                // Si el token existe pero ha expirado, eliminarlo inmediatamente
                reactivationTokenRepository.delete(reactivationToken);
            }
            return false; // Token no válido o expirado
        }

        // Obtener el usuario asociado al token
        Usuario usuario = reactivationToken.getUsuario();

        // Reactivar la cuenta del usuario si no está activa
        if (!usuario.isActivo()) {
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        }

        // Marcar el token como usado
        reactivationToken.marcarComoUsado();
        reactivationTokenRepository.save(reactivationToken);

        // Programar la eliminación del token después de 30 segundos
        eliminarTokenDespuesDeTiempo(reactivationToken, 30);

        return true; // Reactivación exitosa
    }

    private void eliminarTokenDespuesDeTiempo(reactivationToken token, int segundos) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            reactivationTokenRepository.delete(token);
            scheduler.shutdown();
        }, segundos, TimeUnit.SECONDS);
    }




    @Override
    @Transactional
    public void eliminarCuenta(Integer id) throws MessagingException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe"));

        usuarioRepository.delete(usuario);

        // Enviar email de confirmación
        Map<String, Object> model = new HashMap<>();
        model.put("nombre", usuario.getCustomer().getNames());

        Mail mail = emailService.createMail(
                usuario.getEmail(),
                "Cuenta Eliminada",
                model,
                mailFrom
        );

        emailService.sendEmail(mail, "email/cuenta-eliminada-template");
    }
    public void enviarEmailReactivacion(Usuario usuario, String tokens) throws MessagingException {
        Map<String, Object> model = new HashMap<>();
        model.put("nombre", usuario.getEmail());
        model.put("reactivationToken", "http://localhost:4200/reactivar?token=" + tokens);

        Mail mail = emailService.createMail(
                usuario.getEmail(),
                "Reactivación de Cuenta",
                model,
                mailFrom
        );

        System.out.println("Correo creado: " + mail);

        try {
            emailService.sendEmail(mail, "email/reactivacion-cuenta-template");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo: " + e.getMessage());
        }
    }
    @Transactional
    @Override
    public void verificarCuenta(String token) throws BadRequestException {
        Usuario usuario = usuarioRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Token inválido o expirado"));

        usuario.setActivo(true);
        usuario.setVerificationToken(null);
        usuarioRepository.save(usuario);
    }
    @Override
    public UserProfileDTO getUserProfileById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));
        return userMapper.toUserProfileDTO(usuario);
    }

    @Override
    public UserProfileDTO updateUserProfile(Integer id, UserProfileDTO userProfileDTO) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        System.out.println("Usuario encontrado en la base de datos: " + usuario.getId());

        // Verificar si el email o nombre ya existen en otros usuarios (excluyendo al usuario actual)
        if (userProfileDTO.getEmail() != null && !userProfileDTO.getEmail().equals(usuario.getEmail())) {
            boolean existsAsEmail = usuarioRepository.existsByEmail(userProfileDTO.getEmail());
            if (existsAsEmail) {
                throw new IllegalArgumentException("Ya existe un usuario con este email.");
            }
        }

        if (userProfileDTO.getNames() != null && !userProfileDTO.getNames().equals(usuario.getCustomer().getNames())) {
            boolean existsAsCustomer = customerRepository.existsByNames(userProfileDTO.getNames());
            if (existsAsCustomer) {
                throw new IllegalArgumentException("Ya existe un cliente con este nombre.");
            }
        }

        // Actualizar el email en la entidad Usuario si se proporciona
        if (userProfileDTO.getEmail() != null) {
            usuario.setEmail(userProfileDTO.getEmail());
        }

        // Actualizar los datos del usuario solo si el valor no es nulo
        if (usuario.getCustomer() != null) {
            Customer customer = usuario.getCustomer();

            if (userProfileDTO.getEmail() != null) {
                customer.setEmail(userProfileDTO.getEmail());
            }

            if (userProfileDTO.getAddress() != null) {
                customer.setAddress(userProfileDTO.getAddress());
            }

            if (userProfileDTO.getNames() != null) {
                customer.setNames(userProfileDTO.getNames());
            }

            if (userProfileDTO.getIdentificationDocumentId() != null) {
                customer.setIdentificationDocumentId(userProfileDTO.getIdentificationDocumentId());
            }

            if (userProfileDTO.getPhone() != null) {
                customer.setPhone(userProfileDTO.getPhone());
            }

            if (userProfileDTO.getDv() != null) {
                customer.setDv(userProfileDTO.getDv());
            }

            if (userProfileDTO.getIdentification() != null) {
                customer.setIdentification(userProfileDTO.getIdentification());
            }

            if (userProfileDTO.getLegalOrganizationId() != null) {
                customer.setLegalOrganizationId(userProfileDTO.getLegalOrganizationId());
            }

            if (userProfileDTO.getTributeId() != null) {
                customer.setTributeId(userProfileDTO.getTributeId());
            }

            if (userProfileDTO.getMunicipalityId() != null) {
                customer.setMunicipalityId(userProfileDTO.getMunicipalityId());
            }

            // Solo actualizar los campos relacionados con la empresa si el ID del documento es "1"
            if ("1".equals(customer.getIdentificationDocumentId())) {
                if (userProfileDTO.getCompany() != null) {
                    customer.setCompany(userProfileDTO.getCompany());
                }
                if (userProfileDTO.getTradeName() != null) {
                    customer.setTradeName(userProfileDTO.getTradeName());
                }
            }
        }

        usuarioRepository.save(usuario);
        return userMapper.toUserProfileDTO(usuario);
    }





}
