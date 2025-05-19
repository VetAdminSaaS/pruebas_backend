package apiFactus.factusBackend.Dto;

import lombok.Data;

import java.util.List;
@Data
public class SucursalesDetailsDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private String direccion;
        private String telefono;
        private String email;
        private String ciudad;
        private String provincia;
        private String distrito;
        private String referencia;
        private List<String> nombreServicio;


}
