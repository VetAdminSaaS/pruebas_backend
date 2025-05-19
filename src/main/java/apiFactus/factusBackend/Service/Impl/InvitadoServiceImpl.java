package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Invitado;
import apiFactus.factusBackend.Domain.Entity.Role;
import apiFactus.factusBackend.Domain.Entity.mesa;
import apiFactus.factusBackend.Domain.enums.ERole;
import apiFactus.factusBackend.Dto.InvitadoDTO;
import apiFactus.factusBackend.Dto.MesaDTO;
import apiFactus.factusBackend.Mapper.InvitadoMapper;
import apiFactus.factusBackend.Mapper.MesaMapper;
import apiFactus.factusBackend.Repository.InvitadoRepository;
import apiFactus.factusBackend.Repository.MesaRepository;
import apiFactus.factusBackend.Repository.RoleRepository;
import apiFactus.factusBackend.Service.InvitadoService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class InvitadoServiceImpl implements InvitadoService {
    private final InvitadoRepository invitadoRepository;
    private final RoleRepository roleRepository;
    private final MesaRepository mesaRepository;
    private final InvitadoMapper invitadoMapper;
    private final MesaMapper mesaMapper;

    @Override
    public List<InvitadoDTO> getAll() {
        List<Invitado> invitados = invitadoRepository.findAll();
        return invitados.stream()
                .map(invitado -> {
                    InvitadoDTO dto = invitadoMapper.toDTO(invitado);
                    if (invitado.getMesa() != null) {
                        dto.setNumeroMesa(invitado.getMesa().getNumero()); // Añadir el número de mesa
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    public InvitadoDTO findById(Long id){
        Invitado invitado = invitadoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El invitado no existe."));
        return invitadoMapper.toDTO(invitado);
    }
    @Override
    public InvitadoDTO create(InvitadoDTO invitadoDTO) {
        Invitado invitado = invitadoMapper.toEntity(invitadoDTO);
        Role rolInvitado = roleRepository.findByName(ERole.valueOf("INVITADO"))
                .orElseThrow(() -> new RuntimeException("Rol INVITADO no encontrado"));
        invitado.setRol(rolInvitado);

        invitado = invitadoRepository.save(invitado);
        return invitadoMapper.toDTO(invitado);
    }


    @Override
    public InvitadoDTO update(Long id, InvitadoDTO invitadoDTO) {
        Invitado invitadoFromDB = invitadoRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("El invitado no existe con el id: "+id));
        invitadoFromDB.setNombre(invitadoDTO.getNombre());
        invitadoFromDB = invitadoRepository.save(invitadoFromDB);
        return invitadoMapper.toDTO(invitadoFromDB);
    }
    @Override
    public void delete(Long id) {
        Invitado invitado = invitadoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("El invitado no existe con el id: "+id));
        invitadoRepository.delete(invitado);
    }
    @Override
    public List<MesaDTO> getAllMesa() {
        List<mesa> mesas = mesaRepository.findAll();
        return mesas.stream()
                .map(mesaMapper::toDTO)
                .toList();
    }
    @Override
    public MesaDTO findMesaById(Long id) {
        mesa mesaEntity = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La mesa no existe."));
        return mesaMapper.toDTO(mesaEntity);
    }
    @Override
    public MesaDTO create(MesaDTO mesaDTO) {
        mesa mesaEntity = mesaMapper.toEntity(mesaDTO);
        mesaEntity = mesaRepository.save(mesaEntity);
        return mesaMapper.toDTO(mesaEntity);
    }
    @Override
    public MesaDTO update(Long id, MesaDTO mesaDTO) {
        mesa mesaFromDB = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La mesa no existe con el id: " + id));

        mesaFromDB.setNumero(mesaDTO.getNumero());
        mesaFromDB.setCantidad(mesaDTO.getCantidad());

        mesaFromDB = mesaRepository.save(mesaFromDB);
        return mesaMapper.toDTO(mesaFromDB);
    }
    @Override
    public void deleteMesa(Long id) {
        mesa mesaEntity = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La mesa no existe con el id: " + id));

        mesaRepository.delete(mesaEntity);
    }
    @Override
    public InvitadoDTO asignarMesa(Long invitadoId, Long mesaId) {
        Invitado invitado = invitadoRepository.findById(invitadoId)
                .orElseThrow(() -> new ResourceNotFoundException("El invitado no existe con el id: " + invitadoId));

        mesa mesaEntity = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new ResourceNotFoundException("La mesa no existe con el id: " + mesaId));

        // Verificar si la mesa tiene espacio disponible
        if (mesaEntity.getInvitados().size() >= mesaEntity.getCantidad()) {
            throw new IllegalStateException("La mesa " + mesaId + " ya tiene el número máximo de invitados.");
        }

        invitado.setMesa(mesaEntity);
        invitadoRepository.save(invitado);

        // Mapear y devolver el invitado con la mesa asignada
        return invitadoMapper.toDTO(invitado);
    }


    @Override
    public InvitadoDTO retirarDeMesaPorNombre(String nombre) {
        // Buscar el invitado por nombre en lugar de id
        Invitado invitado = invitadoRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("El invitado con el nombre '" + nombre + "' no existe."));

        // Desasociar al invitado de la mesa
        invitado.setMesa(null);

        // Guardar los cambios en la base de datos
        invitadoRepository.save(invitado);

        // Retornar el DTO del invitado
        return invitadoMapper.toDTO(invitado);
    }




}
