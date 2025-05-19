package apiFactus.factusBackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class payment_methodRequestDTO {
    private int id;
    private String codigo;
    private String nombre;
}
