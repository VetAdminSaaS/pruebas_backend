package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.Apoderado;
import apiFactus.factusBackend.Domain.Entity.Mascota;
import apiFactus.factusBackend.Dto.ApoderadoAdminDTO;
import apiFactus.factusBackend.Dto.ApoderadoDTO;
import apiFactus.factusBackend.Dto.ApoderadoResponseDTO;
import apiFactus.factusBackend.Dto.MascotaResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApoderadoMapper {
    private final ModelMapper modelMapper;

    public ApoderadoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public ApoderadoResponseDTO toDetailsDTO(Apoderado apoderado) {
        ApoderadoResponseDTO apoderadoResponseDTO = modelMapper.map(apoderado, ApoderadoResponseDTO.class);
        if (apoderado.getMascotas() != null) {
           apoderadoResponseDTO.setMascotas(
                    apoderado.getMascotas().stream()
                            .map(mascota -> modelMapper.map(mascota, MascotaResponseDTO.class))
                            .collect(Collectors.toList())
            );
        }
        return apoderadoResponseDTO;
    }
    public ApoderadoDTO toDTO(Apoderado apoderado){
        return modelMapper.map(apoderado, ApoderadoDTO.class);
    }

    public Apoderado toEntity(ApoderadoDTO dto) {
        return modelMapper.map(dto, Apoderado.class);
    }
    public Apoderado toEntity(ApoderadoAdminDTO adminDTO) {
        return modelMapper.map(adminDTO, Apoderado.class);
    }

}
