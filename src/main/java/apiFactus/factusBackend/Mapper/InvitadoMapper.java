package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.Invitado;
import apiFactus.factusBackend.Dto.InvitadoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
public class InvitadoMapper {
    private final ModelMapper modelMapper;
    public InvitadoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public InvitadoDTO toDTO(Invitado invitado) {
        return modelMapper.map(invitado, InvitadoDTO.class);
    }
    public Invitado toEntity(InvitadoDTO invitadoDTO) {
        return modelMapper.map(invitadoDTO, Invitado.class);
    }

}
