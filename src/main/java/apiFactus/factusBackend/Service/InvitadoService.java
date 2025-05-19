package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.InvitadoDTO;
import apiFactus.factusBackend.Dto.MesaDTO;

import java.util.List;

public interface InvitadoService {
    List<InvitadoDTO> getAll();

    InvitadoDTO findById(Long id);

    InvitadoDTO create(InvitadoDTO invitadoDTO);

    InvitadoDTO update(Long id, InvitadoDTO invitadoDTO);

    void delete(Long id);

    List<MesaDTO> getAllMesa();

    MesaDTO findMesaById(Long id);

    MesaDTO create(MesaDTO mesaDTO);

    MesaDTO update(Long id, MesaDTO mesaDTO);

    void deleteMesa(Long id);

    InvitadoDTO asignarMesa(Long invitadoId, Long mesaId);



    InvitadoDTO retirarDeMesaPorNombre(String nombre);
}
