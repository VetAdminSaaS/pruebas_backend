package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.MascotaRequestDTO;
import apiFactus.factusBackend.Dto.MascotaResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MascotaService {
    @Transactional
    MascotaResponseDTO crearMascota(MascotaRequestDTO mascotaRequestDTO);

    MascotaResponseDTO obtenerMascotaPorId(Long id);

    List<MascotaResponseDTO> listarMascotas();

    List<MascotaResponseDTO> listarMascotasByApoderado();

    @Transactional
    MascotaResponseDTO actualizarMascota(Long id, MascotaRequestDTO mascotaRequestDTO);

    @Transactional
    void eliminarMascota(Long id);
}
