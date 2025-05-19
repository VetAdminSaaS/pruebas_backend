package apiFactus.factusBackend.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComentarioRequestDTO {
    private Integer rating;
    private String comentario;
    private Integer usuarioId;
    private String nombreUsuario;
    private LocalDateTime createdAt;

}
