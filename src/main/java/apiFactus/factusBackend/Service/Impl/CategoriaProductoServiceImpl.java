package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Categoria;
import apiFactus.factusBackend.Dto.CategoriaDTO;
import apiFactus.factusBackend.Mapper.CategoriaMapper;
import apiFactus.factusBackend.Repository.CategoriaRepository;
import apiFactus.factusBackend.Service.CategoriaService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoriaProductoServiceImpl implements CategoriaService {
    private final CategoriaMapper categoriaMapper;
    private final CategoriaRepository categoriaRepository;


    @Override
    public List<CategoriaDTO> getAll(){
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream()
                .map(categoriaMapper::toDTO)
                .toList();

    }

    @Override
    public CategoriaDTO findById(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria  con ID"+id+"no encontrada"));
        return categoriaMapper.toDTO(categoria);
    }

    @Override
    public CategoriaDTO create(CategoriaDTO categoriaDTO) {
        categoriaRepository.findByNombre(categoriaDTO.getNombre())
                .ifPresent(existingCategoria -> {
                    try {
                        throw new BadRequestException("Categoria ya existente");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });
        Categoria categoria = categoriaMapper.toEntity(categoriaDTO);
        categoria.setCreatedAt(LocalDateTime.now());
        categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(categoria);
    }

    @Override
    public CategoriaDTO update(Integer id, CategoriaDTO categoriaDTO) {
        Categoria categoriaFromDB = categoriaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("La categoria con el ID " + id + "no fue encontrada"));
                categoriaRepository.findByNombre(categoriaDTO.getNombre())
                        .filter(existingCategoria -> !existingCategoria.getId().equals(id))
                        .ifPresent(existingCategoria -> {
                            try {
                                throw  new BadRequestException("Ya existe otra categoria con el mismo nombre");
                            } catch (BadRequestException e) {
                                throw new RuntimeException(e);
                            }
                        });
                categoriaFromDB.setNombre(categoriaDTO.getNombre());
                categoriaFromDB.setDescripcion(categoriaDTO.getDescripcion());
                categoriaFromDB.setUpdatedAt(LocalDateTime.now());

                categoriaFromDB = categoriaRepository.save(categoriaFromDB);
                return categoriaMapper.toDTO(categoriaFromDB);
    }

    @Override
    public void delete(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Categoria  con el ID " + id + "no encontrada"));
        categoriaRepository.delete(categoria);
    }

}
