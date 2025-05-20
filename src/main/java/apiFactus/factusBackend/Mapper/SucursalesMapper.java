package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.ServiciosVeterinarios;
import apiFactus.factusBackend.Domain.Entity.Sucursales;
import apiFactus.factusBackend.Dto.SucursalesDTO;
import apiFactus.factusBackend.Dto.SucursalesDetailsDTO;
import apiFactus.factusBackend.Repository.ServiciosVeterinariosRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SucursalesMapper {
    private final ModelMapper modelMapper;

    public SucursalesMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SucursalesDTO toDTO(Sucursales sucursal) {
        SucursalesDTO dto = modelMapper.map(sucursal, SucursalesDTO.class);
        if (sucursal.getServiciosVeterinarios() != null) {
            List<Long> ids = sucursal.getServiciosVeterinarios().stream()
                    .map(ServiciosVeterinarios::getId)
                    .collect(Collectors.toList());
            Collections.reverse(ids);
            dto.setServiciosVeterinariosIds(ids);
        }
        return dto;
    }

    public Sucursales toEntity(SucursalesDTO sucursalesDTO) {
        return modelMapper.map(sucursalesDTO, Sucursales.class);
    }
    public SucursalesDetailsDTO toDetailsDTO(Sucursales sucursal) {
        SucursalesDetailsDTO dto = modelMapper.map(sucursal, SucursalesDetailsDTO.class);

        if (sucursal.getServiciosVeterinarios() != null) {
            // Extrae los nombres de los servicios y los asigna a la lista en el DTO
            List<String> nombresServicios = sucursal.getServiciosVeterinarios().stream()
                    .map(ServiciosVeterinarios::getNombre)
                    .collect(Collectors.toList());
            dto.setNombreServicio(nombresServicios);
        }

        return dto;
    }

}

