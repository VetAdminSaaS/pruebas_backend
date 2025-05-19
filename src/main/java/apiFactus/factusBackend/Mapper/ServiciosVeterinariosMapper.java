package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.Sucursales;
import apiFactus.factusBackend.Domain.Entity.ServiciosVeterinarios;
import apiFactus.factusBackend.Dto.ServiciosVeterinariosDTO;
import apiFactus.factusBackend.Repository.SucursalesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiciosVeterinariosMapper {
    private final ModelMapper modelMapper;

    public ServiciosVeterinariosMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ServiciosVeterinariosDTO toDTO(ServiciosVeterinarios servicios) {
        return modelMapper.map(servicios, ServiciosVeterinariosDTO.class);
    }

    public ServiciosVeterinarios toEntity(ServiciosVeterinariosDTO serviciosDTO) {
        return modelMapper.map(serviciosDTO, ServiciosVeterinarios.class);
    }
}


