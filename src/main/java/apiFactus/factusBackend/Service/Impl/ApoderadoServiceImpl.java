package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Apoderado;
import apiFactus.factusBackend.Domain.Entity.Role;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Domain.enums.Genero;
import apiFactus.factusBackend.Domain.enums.TipoDocumentoIdentidad;
import apiFactus.factusBackend.Dto.ApoderadoAdminDTO;
import apiFactus.factusBackend.Dto.ApoderadoDTO;
import apiFactus.factusBackend.Dto.ApoderadoResponseDTO;
import apiFactus.factusBackend.Mapper.ApoderadoMapper;
import apiFactus.factusBackend.Repository.ApoderadoRepository;
import apiFactus.factusBackend.Repository.MascotaRepository;
import apiFactus.factusBackend.Repository.RoleRepository;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Service.ApoderadoService;
import apiFactus.factusBackend.exception.BadRequestException;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static apiFactus.factusBackend.Domain.enums.ERole.APODERADO;

@Service
public class ApoderadoServiceImpl implements ApoderadoService {

    private final ApoderadoRepository apoderadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApoderadoMapper apoderadoMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MascotaRepository mascotaRepository;
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Value("${vet.clinic.frontend}")
    private String vetFrontend;

    public ApoderadoServiceImpl(ApoderadoRepository apoderadoRepository, UsuarioRepository usuarioRepository, ApoderadoMapper apoderadoMapper, RoleRepository roleRepository, PasswordEncoder passwordEncoder, EmailService emailService, MascotaRepository mascotaRepository) {
        this.apoderadoRepository = apoderadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.apoderadoMapper = apoderadoMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.mascotaRepository = mascotaRepository;
    }

    @Transactional
    @Override
    public ApoderadoResponseDTO crearApoderado(ApoderadoDTO apoderadoDTO) {
        return registroApoderado(apoderadoDTO, APODERADO);
    }

    private ApoderadoResponseDTO registroApoderado(ApoderadoDTO apoderadoDTO, ERole roleEnum) {
        apoderadoRepository.findByNombreAndApellido(apoderadoDTO.getNombre(), apoderadoDTO.getApellido())
                .ifPresent(apoderado -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un apoderado registrado con el mismo nombre");
                });

        if (usuarioRepository.existsByEmail(apoderadoDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
        }

        if (apoderadoDTO.getPassword() == null || apoderadoDTO.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede estar vacía");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new ResourceNotFoundException("El rol no existe"));

        apoderadoDTO.setPassword(passwordEncoder.encode(apoderadoDTO.getPassword()));
        String verificationToken = UUID.randomUUID().toString();

        Usuario usuario = new Usuario();
        usuario.setEmail(apoderadoDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(apoderadoDTO.getPassword()));
        usuario.setRole(role);
        usuario.setVerificationToken(verificationToken);
        usuario.setActivo(false);
        usuario = usuarioRepository.save(usuario); // Guarda y obtiene el ID generado

        Apoderado apoderado = apoderadoMapper.toEntity(apoderadoDTO);
        apoderado.setUser(usuario); // Asigna el usuario al apoderado
        apoderado.setCreated_At(LocalDateTime.now());
        apoderado = apoderadoRepository.save(apoderado); // Guarda el apoderado con el usuario asociado

        return apoderadoMapper.toDetailsDTO(apoderado);
    }



    @Transactional
    @Override
    public ApoderadoResponseDTO crearApoderadoByAdmin(ApoderadoAdminDTO apoderadoadminDTO, ERole roleEnum) throws MessagingException {
        apoderadoRepository.findByNombreAndApellido(apoderadoadminDTO.getNombre(), apoderadoadminDTO.getApellido())
                .ifPresent(apoderado -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un apoderado registrado con el mismo nombre");
                });

        if (usuarioRepository.existsByEmail(apoderadoadminDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol no existe"));

        String tempPassword = generarContrasenaTemporal();
        String encodedPassword = passwordEncoder.encode(tempPassword);
        String verificationToken = UUID.randomUUID().toString();

        Usuario usuario = new Usuario();
        usuario.setEmail(apoderadoadminDTO.getEmail());
        usuario.setPassword(encodedPassword);
        usuario.setRole(role);
        usuario.setVerificationToken(verificationToken);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        Apoderado apoderado = apoderadoMapper.toEntity(apoderadoadminDTO);
        apoderado.setUser(usuario);
        apoderado.setCreated_At(LocalDateTime.now());
        apoderadoRepository.save(apoderado);

        // Enviar la contraseña solo por correo
        enviarCorreoBienvenida(apoderadoadminDTO, tempPassword, verificationToken);

        return apoderadoMapper.toDetailsDTO(apoderado);
    }

    private String generarContrasenaTemporal() {
        return UUID.randomUUID().toString().substring(0, 8);
    }


    private void enviarCorreoBienvenida(ApoderadoAdminDTO apoderadoDTO, String tempPassword, String verificationToken) throws MessagingException {
        String usuarioEmail = apoderadoDTO.getEmail();
        String verificationLink = vetFrontend + "/verify?token=" + verificationToken;

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioEmail", usuarioEmail);
        model.put("nombre", apoderadoDTO.getNombre());
        model.put("apellido", apoderadoDTO.getApellido());
        model.put("verificationLink", verificationLink);
        model.put("tempPassword", tempPassword);
        Mail mail = emailService.createMail(
                usuarioEmail,
                "Cuenta Temporal - Activación requerida",
                model,
                mailFrom
        );
        emailService.sendEmail(mail, "email/cuenta-temporal-template");
    }




    @Override
    public ApoderadoResponseDTO obtenerDetallesDeApoderado(Long id) {
        Apoderado apoderado = apoderadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un apoderado con el mismo id"));
        return apoderadoMapper.toDetailsDTO(apoderado);
    }

    @Override
    public List<ApoderadoResponseDTO> obtenerApoderados() {
        List<Apoderado> apoderados = apoderadoRepository.findAll();
        return apoderados.stream()
                .map(apoderadoMapper::toDetailsDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ApoderadoResponseDTO actualizarApoderado(Long id, ApoderadoDTO apoderadoDTO) {
        Apoderado apoderadoFromDb = apoderadoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Apoderado no encontrado"));
        apoderadoRepository.findByNombreAndApellido(apoderadoDTO.getNombre(), apoderadoDTO.getApellido())
                .filter(existingApoderado -> !existingApoderado.getId().equals(id))
                .ifPresent(existingApoderado -> {
                    throw new BadRequestException("Ya existe un apoderado con los mismos nombres");
                });
        apoderadoFromDb.setNombre(apoderadoDTO.getNombre());
        apoderadoFromDb.setApellido(apoderadoDTO.getApellido());
        apoderadoFromDb.setDireccion(apoderadoDTO.getDireccion());
        apoderadoFromDb.setProvincia(apoderadoDTO.getProvincia());
        apoderadoFromDb.setDistrito(apoderadoDTO.getDistrito());
        apoderadoFromDb.setGenero(apoderadoDTO.getGenero());
        apoderadoFromDb.setDepartamento(apoderadoDTO.getDepartamento());
        apoderadoFromDb.setEmail(apoderadoDTO.getEmail());
        apoderadoFromDb.setNumeroIdentificacion(apoderadoDTO.getNumeroIdentificacion());
        apoderadoFromDb.setTipoDocumentoIdentidad(apoderadoDTO.getTipoDocumentoIdentidad());
        apoderadoFromDb.setUpdated_At(LocalDateTime.now());
        return apoderadoMapper.toDetailsDTO(apoderadoRepository.save(apoderadoFromDb));


    }
    @Transactional
    public void eliminarApoderado(Long id) {
        Apoderado apoderado = apoderadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apoderado no encontrado"));
        apoderadoRepository.eliminarRelationMascotas(apoderado);
        apoderadoRepository.delete(apoderado);
    }


}
