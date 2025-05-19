package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.ServiciosVeterinarios;
import apiFactus.factusBackend.Dto.ServiciosVeterinariosDTO;
import apiFactus.factusBackend.Mapper.ServiciosVeterinariosMapper;
import apiFactus.factusBackend.Repository.ServiciosVeterinariosRepository;
import apiFactus.factusBackend.Repository.SucursalesRepository;
import apiFactus.factusBackend.Service.ServiciosVeterinariosService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiciosVeterinariosServiceImpl implements ServiciosVeterinariosService {

    private final ServiciosVeterinariosRepository serviciosVeterinariosRepository;
    private final ServiciosVeterinariosMapper serviciosVeterinariosMapper;
    private final SucursalesRepository sucursalesRepository;

    @Override
    public List<ServiciosVeterinariosDTO> getAllServiciosVeterinarios(){
        List<ServiciosVeterinarios> serviciosVeterinarios = serviciosVeterinariosRepository.findAll();
        return serviciosVeterinarios.stream()
                .map(serviciosVeterinariosMapper::toDTO)
                .toList();
    }
    @Override
    public Page<ServiciosVeterinariosDTO> getAllServiciosVeterinarios(Pageable pageable){
        Page<ServiciosVeterinarios> serviciosVeterinarios = serviciosVeterinariosRepository.findAll(pageable);
        return serviciosVeterinarios.map(serviciosVeterinariosMapper::toDTO);
    }
    @Override
    public ServiciosVeterinariosDTO findbyId(Long id){
        ServiciosVeterinarios serviciosVeterinarios = serviciosVeterinariosRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Servicio no encontrado"));
        return serviciosVeterinariosMapper.toDTO(serviciosVeterinarios);

    }
    @Override
    public ServiciosVeterinariosDTO crearServicioVeterinario(ServiciosVeterinariosDTO serviciosVeterinariosDTO){
        serviciosVeterinariosRepository.findByNombre(serviciosVeterinariosDTO.getNombre())
                .ifPresent(existingServicio -> {
                    try{
                        throw  new BadRequestException("El servicio veterinario ya existe");
                    } catch (BadRequestException e) {
                        throw new ResourceNotFoundException(e.getMessage());
                    }
                });

        ServiciosVeterinarios serviciosVeterinarios = serviciosVeterinariosMapper.toEntity(serviciosVeterinariosDTO);
        serviciosVeterinarios.setCreated_At(LocalDateTime.now());
        serviciosVeterinariosRepository.save(serviciosVeterinarios);
        return serviciosVeterinariosMapper.toDTO(serviciosVeterinarios);
    }
    @Override
    public ServiciosVeterinariosDTO updateServicioVeterinario(Long id, ServiciosVeterinariosDTO serviciosVeterinariosDTO) {
        ServiciosVeterinarios serviciosVeterinariosFromDB = serviciosVeterinariosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        // Verificar si ya existe otro servicio con el mismo nombre
        serviciosVeterinariosRepository.findByNombre(serviciosVeterinariosDTO.getNombre())
                .filter(existingServicio -> !existingServicio.getId().equals(id))
                .ifPresent(existingServicio -> {
                    try {
                        throw new BadRequestException("Ya existe un servicio con ese nombre");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });




        // Actualizar datos del servicio
        serviciosVeterinariosFromDB.setNombre(serviciosVeterinariosDTO.getNombre());
        serviciosVeterinariosFromDB.setDescripcion(serviciosVeterinariosDTO.getDescripcion());
        serviciosVeterinariosFromDB.setPrecio(serviciosVeterinariosDTO.getPrecio());
        serviciosVeterinariosFromDB.setDisponible(serviciosVeterinariosDTO.isDisponible());
        serviciosVeterinariosFromDB.setUpdated_At(LocalDateTime.now());
        serviciosVeterinariosFromDB.setCoverPath(serviciosVeterinariosDTO.getCoverPath());

        // Guardar con saveAndFlush para forzar la actualización
        serviciosVeterinariosRepository.saveAndFlush(serviciosVeterinariosFromDB);

        return serviciosVeterinariosMapper.toDTO(serviciosVeterinariosFromDB);
    }


    @Override
    public void delete(Long id){
        ServiciosVeterinarios serviciosVeterinarios = serviciosVeterinariosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        serviciosVeterinariosRepository.delete(serviciosVeterinarios);

    }



}
