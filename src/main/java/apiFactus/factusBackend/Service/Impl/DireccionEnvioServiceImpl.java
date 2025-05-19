package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.DireccionEnvio;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Dto.DireccionEnvioDTO;
import apiFactus.factusBackend.Mapper.DireccionEnvioMapper;
import apiFactus.factusBackend.Repository.DireccionEnvioRepository;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Service.DireccionEnvioService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DireccionEnvioServiceImpl implements DireccionEnvioService {

    private final DireccionEnvioRepository direccionEnvioRepository;
    private final DireccionEnvioMapper direccionEnvioMapper;
    private final UsuarioRepository usuarioRepository;

    /**
     * Método reutilizable para obtener el usuario autenticado.
     */
    private Usuario getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /**
     * Método reutilizable para obtener una dirección de envío por ID.
     */
    private DireccionEnvio getDireccionEnvioById(Long id, Usuario usuario) {
        DireccionEnvio direccionEnvio = direccionEnvioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dirección de envío no encontrada"));

        if (!direccionEnvio.getUsuario().equals(usuario)) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta dirección");
        }
        return direccionEnvio;
    }

    @Override
    public List<DireccionEnvioDTO> getAll() {
        Usuario usuario = getAuthenticatedUser();
        List<DireccionEnvio> direccionEnvios = direccionEnvioRepository.findByUsuario(usuario);
        return direccionEnvios.stream()
                .map(direccionEnvioMapper::toDTO)
                .toList();
    }

    @Override
    public DireccionEnvioDTO findById(Long id) {
        Usuario usuario = getAuthenticatedUser();
        DireccionEnvio direccionEnvio = getDireccionEnvioById(id, usuario);
        return direccionEnvioMapper.toDTO(direccionEnvio);
    }

    @Override
    public DireccionEnvioDTO create(DireccionEnvioDTO direccionEnvioDTO) {
        Usuario usuario = getAuthenticatedUser();

        boolean direccionExistente = direccionEnvioRepository
                .existsByDireccionAndUsuario(direccionEnvioDTO.getDireccion(), usuario);

        if (direccionExistente) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La dirección ya existe para este usuario");
        }

        DireccionEnvio direccionEnvio = direccionEnvioMapper.toEntity(direccionEnvioDTO);
        direccionEnvio.setUsuario(usuario);

        direccionEnvio = direccionEnvioRepository.save(direccionEnvio);
        return direccionEnvioMapper.toDTO(direccionEnvio);
    }

    @Override
    public DireccionEnvioDTO update(Long id, DireccionEnvioDTO direccionEnvioDTO) {
        Usuario usuario = getAuthenticatedUser();
        DireccionEnvio direccionEnvio = getDireccionEnvioById(id, usuario);

        // Verificar que la nueva dirección no esté duplicada en otra entrada del mismo usuario
        boolean direccionExistente = direccionEnvioRepository
                .existsByDireccionAndUsuarioAndIdNot(direccionEnvioDTO.getDireccion(), usuario, id);
        if (direccionExistente) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Otra dirección con estos datos ya existe");
        }

        // Actualizar los campos
        direccionEnvio.setDireccion(direccionEnvioDTO.getDireccion());
        direccionEnvio.setCiudad(direccionEnvioDTO.getCiudad());
        direccionEnvio.setCodigoPostal(direccionEnvioDTO.getCodigoPostal());
        direccionEnvio.setPiso(direccionEnvioDTO.getPiso());
        direccionEnvio.setReferencia(direccionEnvioDTO.getReferencia());
        direccionEnvio.setTelefono(direccionEnvioDTO.getTelefono());
        direccionEnvio.setNombre(direccionEnvioDTO.getNombre());
        direccionEnvio.setProvincia(direccionEnvioDTO.getProvincia());
        direccionEnvio.setDistrito(direccionEnvioDTO.getDistrito());

        // Guardar los cambios
        direccionEnvioRepository.save(direccionEnvio);

        return direccionEnvioMapper.toDTO(direccionEnvio);
    }

    @Override
    public void delete(Long id) {
        Usuario usuario = getAuthenticatedUser();
        DireccionEnvio direccionEnvio = getDireccionEnvioById(id, usuario);
        direccionEnvioRepository.delete(direccionEnvio);
    }
}
