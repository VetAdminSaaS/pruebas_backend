package apiFactus.factusBackend.integration.factus.halltec.Dto;

import lombok.Data;

@Data
public class EventDTO {
    private String number;
    private String cude;
    private String event_code;
    private String event_name;
    private String effective_date;
    private String effective_time;
}

