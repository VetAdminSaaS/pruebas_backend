package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Apoderado;
import apiFactus.factusBackend.Domain.Entity.Mascota;
import apiFactus.factusBackend.Dto.MascotaRequestDTO;
import apiFactus.factusBackend.Dto.MascotaResponseDTO;
import apiFactus.factusBackend.Mapper.MascotaMapper;
import apiFactus.factusBackend.Repository.ApoderadoRepository;
import apiFactus.factusBackend.Repository.MascotaRepository;
import apiFactus.factusBackend.Service.MascotaService;
import apiFactus.factusBackend.integration.notification.email.dto.Mail;
import apiFactus.factusBackend.integration.notification.email.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaMapper mascotaMapper;
    private final ApoderadoRepository apoderadoRepository;
    private final MascotaRepository mascotaRepository;
    private final EmailService emailService;
    @Value("${spring.mail.username}")
    private String mailFrom;

    public MascotaServiceImpl(MascotaMapper mascotaMapper, ApoderadoRepository apoderadoRepository, MascotaRepository mascotaRepository, EmailService emailService) {
        this.mascotaMapper = mascotaMapper;
        this.apoderadoRepository = apoderadoRepository;
        this.mascotaRepository = mascotaRepository;
        this.emailService = emailService;
    }
    @Override
    @Transactional
    public MascotaResponseDTO crearMascota(MascotaRequestDTO mascotaRequestDTO) {

        List<Apoderado> apoderados = apoderadoRepository.findAllById(mascotaRequestDTO.getApoderadoIds());
        if (apoderados.isEmpty()) {
            throw new RuntimeException("No se encontraron apoderados para los IDs proporcionados");
        }
        Mascota mascota = mascotaMapper.toEntity(mascotaRequestDTO, apoderados);
        mascota.setCreated_At(LocalDateTime.now());
        mascotaRepository.save(mascota);
        sendMascotaRegister(mascotaRequestDTO);

        return mascotaMapper.toDetailsDTO(mascota);
    }
    private void sendMascotaRegister(MascotaRequestDTO mascotaRequestDTO){
        String apoderadoEmail = mascotaRequestDTO.getRaza();
        Map<String, Object> model = new HashMap<>();
        model.put("nombre", mascotaRequestDTO.getNombreCompleto());
        model.put("Apoderado", mascotaRequestDTO.getApoderadoIds());

        Mail mail = emailService.createMail(
                apoderadoEmail,
                "Bienvenido" + mascotaRequestDTO.getNombreCompleto() + "a nuestra Familia",
                model,
                mailFrom

        );
    }

    @Override
    public MascotaResponseDTO obtenerMascotaPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        return mascotaMapper.toDetailsDTO(mascota);
    }
    @Override
    public List<MascotaResponseDTO> listarMascotas() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        return mascotas.stream()
                .map(mascotaMapper::toDetailsDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<MascotaResponseDTO> listarMascotasByApoderado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        String emailApoderado = authentication.getName();

        Apoderado apoderado = apoderadoRepository.findByEmail(emailApoderado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apoderado no encontrado"));

        List<Mascota> mascotas = mascotaRepository.findByApoderadosContaining(apoderado);

        return mascotas.stream()
                .map(mascotaMapper::toDetailsDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public MascotaResponseDTO actualizarMascota(Long id, MascotaRequestDTO mascotaRequestDTO) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        // Actualizar datos básicos de la mascota
        mascota.setNombreCompleto(mascotaRequestDTO.getNombreCompleto());
        mascota.setRaza(mascotaRequestDTO.getRaza());
        mascota.setProfilePath(mascotaRequestDTO.getProfilePath());
        mascota.setEspecie(mascotaRequestDTO.getEspecie());
        mascota.setFechaNacimiento(mascotaRequestDTO.getFechaNacimiento());
        mascota.setPeso(mascotaRequestDTO.getPeso());
        mascota.setDescripcion(mascotaRequestDTO.getDescripcion());
        mascota.setEsterilizado(mascotaRequestDTO.getEsterilizado());
        mascota.setUpdated_At(LocalDateTime.now());

        if (mascotaRequestDTO.getApoderadoIds() != null && !mascotaRequestDTO.getApoderadoIds().isEmpty()) {
            List<Apoderado> apoderados = apoderadoRepository.findAllById(mascotaRequestDTO.getApoderadoIds());
            if (apoderados.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron apoderados con los IDs proporcionados");
            }
            mascota.setApoderados(apoderados);
        }

        mascotaRepository.save(mascota);
        return mascotaMapper.toDetailsDTO(mascota);
    }


    @Transactional

    @Override
    public void eliminarMascota(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        mascotaRepository.delete(mascota);
    }

}
