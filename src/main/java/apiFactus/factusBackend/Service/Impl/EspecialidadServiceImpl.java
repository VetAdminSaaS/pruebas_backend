package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Especialidad;
import apiFactus.factusBackend.Dto.EspecialidadDTO;
import apiFactus.factusBackend.Mapper.EspecialidadMapper;
import apiFactus.factusBackend.Repository.EspecialidadRepository;
import apiFactus.factusBackend.Service.EspecialidadService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final EspecialidadMapper especialidadMapper;

    public EspecialidadServiceImpl(EspecialidadRepository especialidadRepository, EspecialidadMapper especialidadMapper) {
        this.especialidadRepository = especialidadRepository;
        this.especialidadMapper = especialidadMapper;
    }

    @Override
    public List<EspecialidadDTO> getAllEspecialidad() {
        List<Especialidad> especialidades = especialidadRepository.findAll();
        return especialidades.stream()
                .map(especialidadMapper::toDto)
                .toList();
    }
    @Override
    public Page<EspecialidadDTO> getAllEspecialidadDTO(Pageable pageable) {
        Page<Especialidad> especialidades = especialidadRepository.findAll(pageable);
        return especialidades.map(especialidadMapper::toDto);
    }
    @Override
    public EspecialidadDTO findById(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Especialidad no existe"));
        return especialidadMapper.toDto(especialidad);
    }
    @Override
    @Transactional
    public EspecialidadDTO createEspecialidad(EspecialidadDTO especialidadDTO) {
        // Verifica si ya existe una especialidad con el mismo nombre
        especialidadRepository.findByNombre(especialidadDTO.getNombre())
                .ifPresent(existingEspecialidad -> {
                    try {
                        throw new BadRequestException("Ya existe una especialidad con el mismo nombre");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Convierte el DTO a entidad
        Especialidad especialidad = especialidadMapper.toEntity(especialidadDTO);

        // Establece la fecha de creación
        especialidad.setCreatedAt(LocalDateTime.now());

        // Guarda la nueva especialidad
        especialidad = especialidadRepository.save(especialidad);

        // Convierte la entidad guardada a DTO y la retorna
        return especialidadMapper.toDto(especialidad);
    }
    @Override
    @Transactional
    public EspecialidadDTO updateEspecialidad(Long id, EspecialidadDTO especialidadDTO) {
        Especialidad especialidadUpdate = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no existe"));
        especialidadRepository.findByNombre(especialidadDTO.getNombre())
                .map(existingEspecialidad -> {
                    if (!existingEspecialidad.getId().equals(id)) {
                        try {
                            throw new BadRequestException("Ya existe otra especialidad creada con el mismo nombre");
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return existingEspecialidad;
                });

        // Actualiza los campos de la especialidad
        especialidadUpdate.setNombre(especialidadDTO.getNombre());
        especialidadUpdate.setUpdatedAt(LocalDateTime.now());

        // Guarda la especialidad actualizada
        especialidadUpdate = especialidadRepository.save(especialidadUpdate);
        return especialidadMapper.toDto(especialidadUpdate);
    }
    @Override
    @Transactional
    public void deleteEspecialidad(Long id) throws BadRequestException {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no existe"));
        try {
            especialidadRepository.delete(especialidad);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("No se puede eliminar la especialidad porque está asociada a otros registros");
        }
    }
}
