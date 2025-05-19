package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Asistencia;
import apiFactus.factusBackend.Domain.Entity.EmpleadoVeterinario;
import apiFactus.factusBackend.Domain.Entity.Sucursales;
import apiFactus.factusBackend.Dto.AsistenciaRequestDTO;
import apiFactus.factusBackend.Repository.AsistenciaRepository;
import apiFactus.factusBackend.Repository.EmpleadoRepository;
import apiFactus.factusBackend.Repository.SucursalesRepository;
import apiFactus.factusBackend.Service.AsistenciaService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {
    private final EmpleadoRepository empleadoRepository;
    private final SucursalesRepository sucursalesRepository;
    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaServiceImpl(EmpleadoRepository empleadoRepository, SucursalesRepository sucursalesRepository, AsistenciaRepository asistenciaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.sucursalesRepository = sucursalesRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    @Override
    public void registrarAsistencia(AsistenciaRequestDTO dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioemail = authentication.getName();

        EmpleadoVeterinario empleado = empleadoRepository.findByUserEmail(usuarioemail)
                .orElseThrow(()-> new ResourceNotFoundException("Empleado no encontrado"));
        Sucursales sucursal = sucursalesRepository.findById(dto.getSucursalId())
                .orElseThrow(()-> new ResourceNotFoundException("Sucursal no encontrad"));
    }
        LocalDate hoy = LocalDate.now();


}
