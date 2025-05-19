package apiFactus.factusBackend.integration.factus.halltec.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BillingPeriodDTO {
    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("end_time")
    private LocalTime endTime;
}
