package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.ServiciosVeterinarios;
import apiFactus.factusBackend.Domain.Entity.Sucursales;
import apiFactus.factusBackend.Dto.SucursalesDTO;
import apiFactus.factusBackend.Dto.SucursalesDetailsDTO;
import apiFactus.factusBackend.Mapper.SucursalesMapper;
import apiFactus.factusBackend.Repository.ServiciosVeterinariosRepository;
import apiFactus.factusBackend.Repository.SucursalesRepository;
import apiFactus.factusBackend.Service.SucursalesService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalesServiceImpl implements SucursalesService {
    private final SucursalesRepository sucursalesRepository;
    private final SucursalesMapper sucursalesMapper;
    private final ServiciosVeterinariosRepository serviciosVeterinariosRepository;

    @Transactional(readOnly = true) // Optimización: Solo lectura, mejor rendimiento
    @Override
    public List<SucursalesDetailsDTO> getAllSucursales() {
        List<Sucursales> sucursalesList = sucursalesRepository.findAll();

        if (sucursalesList.isEmpty()) {
            return Collections.emptyList(); // Evitar `null`
        }

        return sucursalesList.stream()
                .map(sucursalesMapper::toDetailsDTO)
                .toList();
    }

    @Override
    public SucursalesDetailsDTO findById(Long id){
        Sucursales sucursales = sucursalesRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Sucursales not found with id: " + id));
        return sucursalesMapper.toDetailsDTO(sucursales);
    }
    @Override
    @Transactional
    public SucursalesDTO create(SucursalesDTO sucursalesDTO) {
        sucursalesRepository.findByNombre(sucursalesDTO.getNombre())
                .ifPresent(existingSucursal -> {
                    try {
                        throw new BadRequestException("La sucursal ya existe");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });

        List<Long> serviciosIds = sucursalesDTO.getServiciosVeterinariosIds();
        List<ServiciosVeterinarios> servicios = serviciosVeterinariosRepository.findAllById(serviciosIds);
        if (servicios.size() != serviciosIds.size()) {
            throw new ResourceNotFoundException("Uno o más servicios no existen en la base de datos.");
        }
        Sucursales sucursal = sucursalesMapper.toEntity(sucursalesDTO);
        sucursal.setServiciosVeterinarios(servicios);
        sucursal.setCreated_At(LocalDateTime.now());
        sucursal = sucursalesRepository.save(sucursal);

        return sucursalesMapper.toDTO(sucursal);
    }

    @Override
    @Transactional
    public SucursalesDTO update(Long id, SucursalesDTO sucursalesDTO) {
        Sucursales sucursalesFromDB = sucursalesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        sucursalesRepository.findByNombre(sucursalesDTO.getNombre())
                .filter(existingSucursal -> !existingSucursal.getId().equals(id))
                .ifPresent(existingSucursal -> {
                    try {
                        throw new BadRequestException("Ya existe otra sucursal con este nombre");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });

        List<ServiciosVeterinarios> servicios = serviciosVeterinariosRepository.findAllById(sucursalesDTO.getServiciosVeterinariosIds());
        if (servicios.size() != sucursalesDTO.getServiciosVeterinariosIds().size()) {
            throw new ResourceNotFoundException("Uno o más servicios no existen");
        }

        sucursalesFromDB.setNombre(sucursalesDTO.getNombre());
        sucursalesFromDB.setDescripcion(sucursalesDTO.getDescripcion());
        sucursalesFromDB.setCiudad(sucursalesDTO.getCiudad());
        sucursalesFromDB.setTelefono(sucursalesDTO.getTelefono());
        sucursalesFromDB.setEmail(sucursalesDTO.getEmail());
        sucursalesFromDB.setDireccion(sucursalesDTO.getDireccion());
        sucursalesFromDB.setProvincia(sucursalesDTO.getProvincia());
        sucursalesFromDB.setDistrito(sucursalesDTO.getDistrito());
        sucursalesFromDB.setReferencia(sucursalesDTO.getReferencia());
        sucursalesFromDB.setUpdated_At(LocalDateTime.now());
        sucursalesFromDB.setServiciosVeterinarios(servicios);
        sucursalesFromDB = sucursalesRepository.save(sucursalesFromDB);
        return sucursalesMapper.toDTO(sucursalesFromDB);
    }

    @Override
    public void delete(Long id) {
        Sucursales sucursales = sucursalesRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Sucursales not found with id: " + id));
        sucursalesRepository.delete(sucursales);
    }
}
