package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.Categoria;
import apiFactus.factusBackend.Dto.CategoriaDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    private final ModelMapper modelMapper;
    public CategoriaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public CategoriaDTO toDTO(Categoria categoria) {
        return modelMapper.map(categoria, CategoriaDTO.class);
    }
    public Categoria toEntity(CategoriaDTO categoriaDTO) {
        return modelMapper.map(categoriaDTO, Categoria.class);
    }
}
