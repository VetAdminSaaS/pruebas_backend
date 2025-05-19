package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.EmpleadoVeterinario;
import apiFactus.factusBackend.Domain.Entity.Especialidad;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Dto.EmpleadoProfileDTO;
import apiFactus.factusBackend.Dto.EmpleadoRegistrationDTO;
import apiFactus.factusBackend.Dto.EmpleadosDTO;
import apiFactus.factusBackend.Dto.EmpleadosDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmpleadoMapper {
    private final ModelMapper modelMapper;
    public EmpleadoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public EmpleadosDetailsDTO toDetailsDto(EmpleadoVeterinario empleado) {
        EmpleadosDetailsDTO empleadosDTO = modelMapper.map(empleado, EmpleadosDetailsDTO.class);



        // Obtener nombres de los servicios
        if (empleado.getServicios() != null && !empleado.getServicios().isEmpty()) {
            List<String> nombresServicios = empleado.getServicios().stream()
                    .map(servicio -> servicio.getServicio().getNombre())
                    .collect(Collectors.toList());
            empleadosDTO.setNombreServicio(nombresServicios);
        }

        if (empleado.getEspecialidades() != null && !empleado.getEspecialidades().isEmpty()) {
            List<String> nombresEspecialidades = empleado.getEspecialidades().stream()
                    .map(Especialidad::getNombre)
                    .collect(Collectors.toList());
            empleadosDTO.setEspecialidadesNombres(nombresEspecialidades);
        }

        // Obtener nombre de la sucursal
        if (empleado.getSucursal() != null) {
            empleadosDTO.setSucursalName(empleado.getSucursal().getNombre());
        }

        return empleadosDTO;
    }





    public EmpleadoVeterinario toEntity(EmpleadosDTO empleadosDTO) {
        return modelMapper.map(empleadosDTO, EmpleadoVeterinario.class);
    }
    public EmpleadosDTO toDto(EmpleadoVeterinario empleado) {
        EmpleadosDTO empleadosDTO = modelMapper.map(empleado, EmpleadosDTO.class);
        if (empleado.getServicios() != null && !empleado.getServicios().isEmpty()) {
            List<String> nombresServicios = empleado.getServicios().stream()
                    .map(servicio -> servicio.getServicio().getNombre())
                    .collect(Collectors.toList());
            empleadosDTO.setNombreServicio(nombresServicios);
        }
        if (empleado.getEspecialidades() != null && !empleado.getEspecialidades().isEmpty()) {
            List<String> nombresEspecialidades = empleado.getEspecialidades().stream()
                    .map(Especialidad::getNombre)
                    .collect(Collectors.toList());
            empleadosDTO.setEspecialidadesNombres(nombresEspecialidades);
        }
        // Obtener nombre de la sucursal
        if (empleado.getSucursal() != null) {
            empleadosDTO.setSucursalName(empleado.getSucursal().getNombre());
        }

        return empleadosDTO;
    }

    public EmpleadoVeterinario toEntityRegistration(EmpleadoRegistrationDTO empleadosDTO) {
        EmpleadoVeterinario empleado = modelMapper.map(empleadosDTO, EmpleadoVeterinario.class);

        // Verificamos si el empleado no tiene un usuario asociado y lo creamos
        if (empleado.getUser() == null) {
            Usuario usuario = new Usuario();
            usuario.setEmail(empleadosDTO.getEmail());
            usuario.setPassword(empleadosDTO.getPassword()); // Asegúrate de que esto se encripte antes de guardar
            empleado.setUser(usuario);
        }

        return empleado;
    }

    public EmpleadoProfileDTO toEmpleadoUserProfileDTO(EmpleadoVeterinario empleado) {
        // Mapear los campos básicos con ModelMapper
        EmpleadoProfileDTO empleadoProfileDTO = modelMapper.map(empleado, EmpleadoProfileDTO.class);

        // Mapear especialidades si no están vacías
        if (empleado.getEspecialidades() != null && !empleado.getEspecialidades().isEmpty()) {
            List<String> nombresEspecialidades = empleado.getEspecialidades().stream()
                    .map(Especialidad::getNombre)
                    .toList();
            empleadoProfileDTO.setEspecialidades(nombresEspecialidades);
        }

        // Mapear servicios si no están vacíos
        if (empleado.getServicios() != null && !empleado.getServicios().isEmpty()) {
            List<Long> idsServicios = empleado.getServicios().stream()
                    .map(servicio -> servicio.getServicio().getId()) // Obtener ID del servicio
                    .toList();
            empleadoProfileDTO.setServiciosIds(idsServicios);
        }

        // Mapear la sucursal
        if (empleado.getSucursal() != null) {
            empleadoProfileDTO.setSucursalId(empleado.getSucursal().getId());
        }

        return empleadoProfileDTO;
    }

}
