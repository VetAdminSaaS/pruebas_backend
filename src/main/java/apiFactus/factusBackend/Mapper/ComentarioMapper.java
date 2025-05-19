package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.ComentarioProducto;
import apiFactus.factusBackend.Dto.ComentarioRequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ComentarioMapper {
    private final ModelMapper modelMapper;
    public ComentarioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public ComentarioRequestDTO toDto(ComentarioProducto comentarioProducto) {
        return modelMapper.map(comentarioProducto, ComentarioRequestDTO.class);
    }
    public ComentarioProducto toEntity(ComentarioRequestDTO comentarioDTO) {
        return modelMapper.map(comentarioDTO, ComentarioProducto.class);
    }

}
