package apiFactus.factusBackend.integration.factus.halltec.Dto;

import lombok.Data;

import java.util.List;

@Data
public class FactusEventResponseDTO {
    private String status;
    private String message;
    private List<EventDTO> data;
}
