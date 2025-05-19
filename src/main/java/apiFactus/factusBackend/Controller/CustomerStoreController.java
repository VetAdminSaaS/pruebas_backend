package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerStoreController {
    private final StoreService storeService;

    @GetMapping("/last-six")
    public ResponseEntity<List<PurchaseDTO>> getLastSixPaidPurchaseByAuthenticationUser(){
        List<PurchaseDTO> purchases = storeService.getLastSixPaidPurchasesByAuthenticatedUser();
        return ResponseEntity.ok(purchases);
    }
}
