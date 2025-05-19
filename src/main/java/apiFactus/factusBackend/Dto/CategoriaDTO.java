package apiFactus.factusBackend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaDTO {
    private Integer id;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre debe tener 50 caracteres o menos")
    private String nombre;
    @NotBlank(message = "La descripcion es obligatorio")
    @Size(max = 500, message = "La descripcion debe tener 500 caracteres o menos")
    private String descripcion;

}
