package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Dto.UserProfileDTO;
import apiFactus.factusBackend.Dto.UsuariosStoreDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StoreService {


    UsuariosStoreDTO obtenerUsuarioAutenticado();



    @Transactional(readOnly = true)
    List<UserProfileDTO> findAll();

    List<PurchaseDTO> getLastSixPaidPurchasesByAuthenticatedUser();
}
