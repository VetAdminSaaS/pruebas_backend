package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.Mascota;
import apiFactus.factusBackend.Domain.Entity.Apoderado;
import apiFactus.factusBackend.Dto.MascotaRequestDTO;
import apiFactus.factusBackend.Dto.MascotaResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MascotaMapper {
    private final ModelMapper modelMapper;

    public MascotaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MascotaResponseDTO toDetailsDTO(Mascota mascota) {
        MascotaResponseDTO dto = modelMapper.map(mascota, MascotaResponseDTO.class);
        if (mascota.getApoderados() != null) {
            dto.setApoderadosNames(
                    mascota.getApoderados().stream()
                            .map(Apoderado::getNombre)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    public Mascota toEntity(MascotaRequestDTO dto, List<Apoderado> apoderados) {
        Mascota mascota = modelMapper.map(dto, Mascota.class);
        mascota.setApoderados(apoderados);
        return mascota;
    }

}
