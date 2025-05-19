package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.Especialidad;
import apiFactus.factusBackend.Dto.EspecialidadDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EspecialidadMapper {
    private final ModelMapper modelMapper;
    public EspecialidadMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public EspecialidadDTO toDto(Especialidad especialidad) {
        return modelMapper.map(especialidad, EspecialidadDTO.class);
    }
    public Especialidad toEntity(EspecialidadDTO especialidadDTO) {
        return modelMapper.map(especialidadDTO, Especialidad.class);
    }
}
