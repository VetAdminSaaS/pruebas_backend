package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.mesa;
import apiFactus.factusBackend.Dto.MesaDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
public class MesaMapper {
    private final ModelMapper modelMapper;

    public MesaMapper() {
        this.modelMapper = new ModelMapper();
    }

    public MesaDTO toDTO(mesa entity) { // 🔹 Recibe una entidad 'mesa'
        return modelMapper.map(entity, MesaDTO.class);
    }

    public mesa toEntity(MesaDTO dto) { // 🔹 Recibe un DTO y lo convierte en entidad
        return modelMapper.map(dto, mesa.class);
    }
}
