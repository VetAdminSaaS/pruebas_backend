package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.PurchaseCreateUpadteDTO;
import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class PurchaseController {
    private final PurchaseService purchaseService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PurchaseDTO>> listarTodasCompras() {
        List<PurchaseDTO> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @PostMapping
    public ResponseEntity<PurchaseDTO> crearCompra(@RequestBody PurchaseCreateUpadteDTO purchaseCreateUpadteDTO) {
        System.out.println("Received Purchase DTO: " + purchaseCreateUpadteDTO);

        PurchaseDTO purchaseDTO = purchaseService.createPurchase(purchaseCreateUpadteDTO);
        return new ResponseEntity<>(purchaseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<PurchaseDTO> confirmPurchase(@PathVariable Integer id) {
        PurchaseDTO confirmedPurchase = purchaseService.confirmPurchase(id);
        return ResponseEntity.ok(confirmedPurchase);
    }
    @GetMapping("/{id}/status")
    public ResponseEntity<PurchaseDTO> getPurchaseStatus(@PathVariable Integer id) {
        PurchaseDTO purchase = purchaseService.getPurchaseById(id);
        if (purchase == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(purchase);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable Integer id) {
        PurchaseDTO purchase = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchase);
    }
    @GetMapping("/total-paid")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Double> getTotalPaidPurchases() {
        double total = purchaseService.getTotalPaidPurchases();
        return ResponseEntity.ok(total);
    }
    @GetMapping("/user")
    public ResponseEntity<List<PurchaseDTO>> getPurchaseById() {
        List<PurchaseDTO> purchase = purchaseService.getPurchaseHistoryByUserId();
        return ResponseEntity.ok(purchase);
    }
    @GetMapping("/facturas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> contarCompras() {
        return ResponseEntity.ok(purchaseService.contarComprasConNumero());
    }
    @GetMapping("/sales/this-month")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Double>> obtenerVentasDelMes() {
        return ResponseEntity.ok(purchaseService.obtenerVentasMesActualYAnterior());
    }

    @GetMapping("/sales-comparison")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerComparacionVentas() {
        return ResponseEntity.ok(purchaseService.obtenerComparacionVentas());
    }

}
