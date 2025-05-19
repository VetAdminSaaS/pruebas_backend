package apiFactus.factusBackend.Dto;

import lombok.Data;

@Data
public class DireccionEnvioDTO {
    private Long id;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private Integer piso;
    private String referencia;
    private String telefono;
    private String nombre;
    private String provincia;
    private String distrito;
}
