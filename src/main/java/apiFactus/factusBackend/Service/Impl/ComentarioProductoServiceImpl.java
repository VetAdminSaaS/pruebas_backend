package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.ComentarioProducto;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import apiFactus.factusBackend.Dto.ComentarioRequestDTO;
import apiFactus.factusBackend.Mapper.ComentarioMapper;
import apiFactus.factusBackend.Repository.ComentarioProductoRepository;
import apiFactus.factusBackend.Repository.ProductoRepository;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Service.ComentarioProductoService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioProductoServiceImpl implements ComentarioProductoService {

    private final ComentarioProductoRepository comentarioProductoRepository;
    private final ComentarioMapper comentarioMapper;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<ComentarioRequestDTO> getAllComentariosPorProducto(Integer productoId) {
        if (!productoRepository.existsById(Long.valueOf(productoId))) {
            throw new EntityNotFoundException("El producto con ID " + productoId + " no existe.");
        }

        List<ComentarioProducto> comentarios = comentarioProductoRepository.findByProductoId(productoId);

        return comentarios.stream().map(comentario -> {
            ComentarioRequestDTO dto = comentarioMapper.toDto(comentario);
            dto.setCreatedAt(comentario.getCreatedAt());

            if (comentario.getUsuario() != null && comentario.getUsuario().getCustomer() != null) {
                dto.setUsuarioId(comentario.getUsuario().getId());
                dto.setNombreUsuario(comentario.getUsuario().getCustomer().getNames()); // Ahora el nombre está en Customer
            } else {
                dto.setUsuarioId(null);
                dto.setNombreUsuario("Usuario desconocido");
            }

            return dto;
        }).toList();
    }


    @Override
    public Page<ComentarioRequestDTO> getComentariosPorProducto(Integer productoId, Pageable pageable) {
        Page<ComentarioProducto> comentarios = comentarioProductoRepository.findByProductoId(productoId, pageable);
        return comentarios.map(comentarioMapper::toDto);
    }
    @Override
    public ComentarioRequestDTO finById(Integer id){
        ComentarioProducto comentarioProducto = comentarioProductoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("El producto con ID " + id + " no existe."));
        return comentarioMapper.toDto(comentarioProducto);
    }


    @Override
    public ComentarioRequestDTO create(ComentarioRequestDTO comentarioRequestDTO, Integer productoId) {
        if (comentarioRequestDTO == null || comentarioRequestDTO.getComentario() == null || comentarioRequestDTO.getComentario().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }

        productos_Tienda producto = productoRepository.findById(productoId.longValue())
                .orElseThrow(() -> new EntityNotFoundException("El producto con ID " + productoId + " no existe."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new SecurityException("Usuario no autenticado.");
        }

        String email;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("El usuario autenticado no existe en la base de datos."));

        // Verificar si el usuario ya ha comentado sobre este producto
        boolean yaComento = comentarioProductoRepository.existsByProductoIdAndUsuarioId(productoId, Long.valueOf(usuario.getId()));
        if (yaComento) {
            throw new IllegalStateException("Ya has comentado sobre este producto. Solo se permite una reseña por usuario.");
        }

        // Obtener el nombre desde Customer directamente
        String nombreUsuario = (usuario.getCustomer() != null) ? usuario.getCustomer().getNames() : "Usuario desconocido";

        // Crear y guardar el comentario
        ComentarioProducto comentarioProducto = comentarioMapper.toEntity(comentarioRequestDTO);
        comentarioProducto.setProducto(producto);
        comentarioProducto.setUsuario(usuario);
        comentarioProducto.setCreatedAt(LocalDateTime.now());
        comentarioProducto = comentarioProductoRepository.save(comentarioProducto);

        // Construir el DTO de respuesta
        ComentarioRequestDTO responseDTO = comentarioMapper.toDto(comentarioProducto);
        responseDTO.setUsuarioId(usuario.getId());
        responseDTO.setNombreUsuario(nombreUsuario);

        return responseDTO;
    }


    @Transactional
    @Override
    public ComentarioRequestDTO update(Integer id, ComentarioRequestDTO comentarioRequestDTO){
        ComentarioProducto comentarioProductoFromDb = comentarioProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID " + id + " no existe."));
        comentarioProductoFromDb.setComentario(comentarioRequestDTO.getComentario());
        comentarioProductoFromDb.setRating(comentarioRequestDTO.getRating());
        comentarioProductoFromDb.setUpdatedAt(LocalDateTime.now());

        comentarioProductoFromDb = comentarioProductoRepository.save(comentarioProductoFromDb);
        return comentarioMapper.toDto(comentarioProductoFromDb);
    }
    @Transactional
    @Override
    public void delete(Integer id) {
        ComentarioProducto comentarioProducto = comentarioProductoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("El comentario con ID " + id + " no existe."));
        comentarioProductoRepository.delete(comentarioProducto);
    }

    @Override
    public Double obtenePromedioRatingProducto(Integer productoId) {
        Double promedio = comentarioProductoRepository.obtenerPromedioPorProducto(Long.valueOf(productoId));
        return promedio != null ? promedio : 0.0;
    }





}
