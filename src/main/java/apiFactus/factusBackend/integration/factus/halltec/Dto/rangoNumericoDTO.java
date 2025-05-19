package apiFactus.factusBackend.integration.factus.halltec.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class rangoNumericoDTO {
    private Integer id;
    private String document;
    private String prefix;
    private Integer from;
    private Integer to;
    private String resolution_number;
    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("technical_key")
    private String technicalKey;

    @JsonProperty("is_expired")
    private Boolean isExpired;

    @JsonProperty("is_active")
    private Integer isActive;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}

