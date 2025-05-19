package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.*;
import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Domain.enums.TipoEmpleado;
import apiFactus.factusBackend.Dto.EmpleadoProfileDTO;
import apiFactus.factusBackend.Dto.EmpleadoRegistrationDTO;
import apiFactus.factusBackend.Dto.EmpleadosDTO;
import apiFactus.factusBackend.Dto.EmpleadosDetailsDTO;
import apiFactus.factusBackend.Mapper.EmpleadoMapper;
import apiFactus.factusBackend.Mapper.UserMapper;
import apiFactus.factusBackend.Repository.*;
import apiFactus.factusBackend.Service.EmpleadoService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoMapper empleadoMapper;
    private final SucursalesRepository sucursalesRepository;
    private final ServiciosVeterinariosRepository serviciosVeterinariosRepository;
    private final UsuarioRepository usuarioRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final EspecialidadRepository especialidadRepository;
    private final EmpleadoServicioRepository empleadoServicioRepository;
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Value("${vet.clinic.frontend}")
    private String vetFrontend;
    @Override
    public List<EmpleadosDetailsDTO> getAll(){
        List<EmpleadoVeterinario> empleadosVeterinario = empleadoRepository.findAll();
        return  empleadosVeterinario.stream()
                .map(empleadoMapper::toDetailsDto)
                .toList();
    }
    @Override
    public EmpleadosDetailsDTO findById(Long id) {
        EmpleadoVeterinario empleadoVeterinario = empleadoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Empleado con id:"+ id+ " no encontrado"));
        return empleadoMapper.toDetailsDto(empleadoVeterinario);
    }
    @Override
    public EmpleadoRegistrationDTO crearEmpleado(EmpleadoRegistrationDTO empleadosDTO) throws BadRequestException, MessagingException {
        // Verificar si el empleado ya existe por nombre y apellido
        empleadoRepository.findByNombreAndApellido(empleadosDTO.getNombre(), empleadosDTO.getApellido())
                .ifPresent(empleado -> {
                    throw new RuntimeException(new BadRequestException("El nombre del empleado ya existe"));
                });

        // Verificar si el email ya está registrado
        if (usuarioRepository.existsByEmail(empleadosDTO.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        Sucursales sucursal = sucursalesRepository.findById(empleadosDTO.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + empleadosDTO.getSucursalId()));

        // Buscar especialidades por sus IDs
        List<Long> especialidadIds = empleadosDTO.getEspecialidadIds();
        List<Especialidad> especialidades = especialidadRepository.findAllById(especialidadIds);
        if (especialidades.size() != especialidadIds.size()) {
            throw new BadRequestException("Algunas especialidades no existen en la base de datos");
        }

        // Buscar servicios por sus IDs
        List<Long> serviciosIds = empleadosDTO.getServiciosIds();
        List<ServiciosVeterinarios> servicios = serviciosVeterinariosRepository.findAllById(serviciosIds);
        if (servicios.size() != serviciosIds.size()) {
            throw new BadRequestException("Algunos servicios no existen en la base de datos");
        }
        EmpleadoVeterinario empleadoVeterinario = empleadoMapper.toEntityRegistration(empleadosDTO);
        empleadoVeterinario.setCreated_At(LocalDateTime.now());
        empleadoVeterinario.setEstado(empleadosDTO.getEstado());
        empleadoVeterinario.setEspecialidades(especialidades);
        empleadoVeterinario.setSucursal(sucursal);
        empleadoVeterinario = empleadoRepository.save(empleadoVeterinario);

        EmpleadoVeterinario finalEmpleadoVeterinario = empleadoVeterinario;
        List<EmpleadoServicio> empleadoServicios = servicios.stream()
                .map(servicio -> new EmpleadoServicio(finalEmpleadoVeterinario, servicio))
                .toList();

        empleadoVeterinario.setServicios(empleadoServicios);
        empleadoVeterinario = empleadoRepository.save(empleadoVeterinario); // Guardamos con los servicios

        empleadoServicioRepository.saveAll(empleadoServicios);


        String verificationToken = UUID.randomUUID().toString();
        sendRegisterConfirmation(empleadosDTO, verificationToken);

        return empleadosDTO;
    }
    @Override
    @Transactional
    public EmpleadosDetailsDTO update(Long id, EmpleadosDetailsDTO empleadoUpdate) {
        EmpleadoVeterinario empleadoVeterinarioFromDB = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con id: " + id + " no encontrado"));

        // Verificar duplicados de nombre y apellido
        empleadoRepository.findByNombreAndApellido(empleadoUpdate.getNombre(), empleadoUpdate.getApellido())
                .filter(existingEmpleado -> !existingEmpleado.getId().equals(id))
                .ifPresent(existingEmpleado -> {
                    try {
                        throw new BadRequestException("Ya existe un empleado con el mismo nombre y apellido");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Obtener sucursal
        Sucursales sucursal = sucursalesRepository.findById(empleadoUpdate.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + empleadoUpdate.getSucursalId()));

        // Obtener especialidades
        List<Especialidad> especialidades = especialidadRepository.findAllById(empleadoUpdate.getEspecialidadIds());

        // Obtener servicios veterinarios
        List<ServiciosVeterinarios> servicios = serviciosVeterinariosRepository.findAllById(empleadoUpdate.getServiciosIds());

        // 🔥 ELIMINAR SERVICIOS ANTIGUOS EXPLÍCITAMENTE
        empleadoServicioRepository.deleteByEmpleadoVeterinarioId(empleadoVeterinarioFromDB.getId());
        empleadoVeterinarioFromDB.getServicios().clear();

        // Guardar la eliminación antes de agregar los nuevos servicios
        empleadoRepository.save(empleadoVeterinarioFromDB);

        // 🔄 ASIGNAR NUEVOS SERVICIOS
        EmpleadoVeterinario finalEmpleadoVeterinarioFromDB = empleadoVeterinarioFromDB;
        List<EmpleadoServicio> empleadoServicios = servicios.stream()
                .map(servicio -> new EmpleadoServicio(finalEmpleadoVeterinarioFromDB, servicio))
                .toList();

        empleadoVeterinarioFromDB.getServicios().addAll(empleadoServicios);

        // 🔄 ACTUALIZAR EMPLEADO
        empleadoVeterinarioFromDB.setNombre(empleadoUpdate.getNombre());
        empleadoVeterinarioFromDB.setApellido(empleadoUpdate.getApellido());
        empleadoVeterinarioFromDB.setEmail(empleadoUpdate.getEmail());
        empleadoVeterinarioFromDB.setFechaNacimiento(empleadoUpdate.getFechaNacimiento());
        empleadoVeterinarioFromDB.setDireccion(empleadoUpdate.getDireccion());
        empleadoVeterinarioFromDB.setProfilePath(empleadoUpdate.getProfilePath());
        empleadoVeterinarioFromDB.setUpdated_At(LocalDateTime.now());
        empleadoVeterinarioFromDB.setSucursal(sucursal);
        empleadoVeterinarioFromDB.setTipoDocumentoIdentidad(empleadoUpdate.getTipoDocumentoIdentidad());
        empleadoVeterinarioFromDB.setGenero(empleadoUpdate.getGenero());
        empleadoVeterinarioFromDB.setFechaContratacion(empleadoUpdate.getFechaContratacion());
        empleadoVeterinarioFromDB.setEstado(empleadoUpdate.getEstado());
        empleadoVeterinarioFromDB.setTelefono(empleadoUpdate.getTelefono());
        empleadoVeterinarioFromDB.setEspecialidades(especialidades);
        empleadoVeterinarioFromDB.setTipoEmpleado(empleadoUpdate.getTipoEmpleado());

        // GUARDAR LOS CAMBIOS
        empleadoVeterinarioFromDB = empleadoRepository.save(empleadoVeterinarioFromDB);

        return empleadoMapper.toDetailsDto(empleadoVeterinarioFromDB);
    }




    @Override
    @Transactional
    public void delete(Long id) {
        EmpleadoVeterinario empleadoVeterinario = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        Usuario usuario = empleadoVeterinario.getUser();
        empleadoRepository.delete(empleadoVeterinario);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }

    @Transactional
    @Override
    public EmpleadoProfileDTO registroEmpleadoVeterinario(EmpleadoRegistrationDTO empleadoRegistrationDTO) throws MessagingException, BadRequestException {
        return registroUserWithRole(empleadoRegistrationDTO, ERole.EMPLEADO);
    }

    private EmpleadoProfileDTO registroUserWithRole(EmpleadoRegistrationDTO empleadoRegistrationDTO, ERole roleEnum)
            throws MessagingException, BadRequestException {

        // Validaciones previas
        if (usuarioRepository.existsByEmail(empleadoRegistrationDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (customerRepository.existsByNames(empleadoRegistrationDTO.getNombre())) {
            throw new IllegalArgumentException("El nombre ya está registrado");
        }

        // Recuperar sucursal y rol
        Sucursales sucursal = sucursalesRepository.findById(empleadoRegistrationDTO.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con el ID: " + empleadoRegistrationDTO.getSucursalId()));

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new ResourceNotFoundException("El rol no existe"));

        // Crear y configurar usuario
        String encodedPassword = passwordEncoder.encode(empleadoRegistrationDTO.getPassword());
        String verificationToken = UUID.randomUUID().toString();

        Usuario usuario = new Usuario();
        usuario.setEmail(empleadoRegistrationDTO.getEmail());
        usuario.setPassword(encodedPassword);
        usuario.setRole(role);
        usuario.setVerificationToken(verificationToken);
        usuario.setActivo(false);
        usuario = usuarioRepository.save(usuario);

        // Crear y configurar empleado
        EmpleadoVeterinario empleado = empleadoMapper.toEntityRegistration(empleadoRegistrationDTO);
        empleado.setUser(usuario);
        empleado.setCreated_At(LocalDateTime.now());
        empleado.setSucursal(sucursal);
        empleado.setTipoEmpleado(empleadoRegistrationDTO.getTipoEmpleado());
        empleado.setEstado(true);

        if(empleado.getTipoEmpleado() == TipoEmpleado.VETERINARIO) {
            // Asignar especialidades
            List<Long> especialidadIds = empleadoRegistrationDTO.getEspecialidadIds();
            if (especialidadIds == null) {
                especialidadIds = Collections.emptyList();
            }
            List<Especialidad> especialidades = especialidadRepository.findAllById(especialidadIds);
            if (especialidades.size() != especialidadIds.size()) {
                throw new BadRequestException("Algunas especialidades no existen en la base de datos");
            }
            empleado.setEspecialidades(new ArrayList<>(especialidades));
        } else if (empleadoRegistrationDTO.getEspecialidadIds() != null && !empleadoRegistrationDTO.getEspecialidadIds().isEmpty()) {
            throw new BadRequestException("Solo los veterinarios pueden tener especialidades asignadas.");
        } else {
            empleado.setEspecialidades(new ArrayList<>());
        }
        List<Long> serviciosIds = empleadoRegistrationDTO.getServiciosIds();
        if (serviciosIds == null) {
            serviciosIds = Collections.emptyList();
        }
        List<ServiciosVeterinarios> servicios = serviciosVeterinariosRepository.findAllById(serviciosIds);
        if (servicios.size() != serviciosIds.size()) {
            throw new BadRequestException("Algunos servicios no existen en la base de datos");
        }
        if (!servicios.isEmpty()) {
            EmpleadoVeterinario finalEmpleado = empleado;
            List<EmpleadoServicio> empleadoServicios = servicios.stream()
                    .map(servicio -> new EmpleadoServicio(finalEmpleado, servicio))
                    .toList();
            empleado.setServicios(empleadoServicios);
        } else {
            empleado.setServicios(new ArrayList<>());
        }
        empleado = empleadoRepository.save(empleado);
        if (empleado.getServicios() != null && !empleado.getServicios().isEmpty()) {
            empleadoServicioRepository.saveAll(empleado.getServicios());
        }
        sendRegisterConfirmation(empleadoRegistrationDTO, verificationToken);

        return empleadoMapper.toEmpleadoUserProfileDTO(empleado);
    }

    private void sendRegisterConfirmation(EmpleadoRegistrationDTO empleadoRegistrationDTO, String verificationToken) throws MessagingException {
        String usuarioEmail = empleadoRegistrationDTO.getEmail();
        String verificationLink = vetFrontend + "/verify?token=" + verificationToken;

        Map<String, Object> model = new HashMap<>();
        model.put("usuarioEmail", usuarioEmail);
        model.put("nombre", empleadoRegistrationDTO.getNombre());
        model.put("apellido", empleadoRegistrationDTO.getApellido());
        model.put("verificationLink", verificationLink);

        Mail mail = emailService.createMail(
                usuarioEmail,
                "Bienvenido a nuestra Familia",
                model,
                mailFrom
        );

        emailService.sendEmail(mail, "email/bienvenido-empleado-template");
    }
    @Override
    public EmpleadosDTO completarRegistro(Long id, EmpleadosDTO empleadoDTO) {
        EmpleadoVeterinario empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        empleado.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
        empleado.setDireccion(empleadoDTO.getDireccion());
        empleado.setTelefono(empleadoDTO.getTelefono());
        empleado.setProfilePath(empleadoDTO.getProfilePath());
        empleado.setUpdated_At(LocalDateTime.now());
        empleadoRepository.save(empleado);

        return empleadoMapper.toDto(empleado);
    }




}
