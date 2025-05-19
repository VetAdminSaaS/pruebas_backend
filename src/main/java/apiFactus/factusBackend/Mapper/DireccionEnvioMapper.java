package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.DireccionEnvio;
import apiFactus.factusBackend.Dto.DireccionEnvioDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class DireccionEnvioMapper {
    private final ModelMapper modelMapper;
    public DireccionEnvioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

    }
    public DireccionEnvioDTO toDTO(DireccionEnvio direccionEnvio) {
        return modelMapper.map(direccionEnvio, DireccionEnvioDTO.class);
    }
    public DireccionEnvio toEntity(DireccionEnvioDTO direccionEnvioDTO) {
        return modelMapper.map(direccionEnvioDTO, DireccionEnvio.class);
    }
}
