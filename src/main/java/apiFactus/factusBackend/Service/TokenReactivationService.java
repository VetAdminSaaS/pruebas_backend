package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.Entity.reactivationToken;

public interface TokenReactivationService {
    reactivationToken generarToken(Usuario usuario);

    void validarYReactivarCuenta(String token);
}
