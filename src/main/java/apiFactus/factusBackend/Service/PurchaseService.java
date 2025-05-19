package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Domain.enums.ShipmentStatus;
import apiFactus.factusBackend.Dto.PurchaseCreateUpadteDTO;
import apiFactus.factusBackend.Dto.PurchaseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface PurchaseService {
    @Transactional
    PurchaseDTO createPurchase(PurchaseCreateUpadteDTO purchaseDTO);

    List<PurchaseDTO> getAllPurchases();

    double getTotalPaidPurchases();

    Long contarComprasConNumero();

    Double obtenerTotalVentasMesActual();


    Map<String, Double> obtenerVentasMesActualYAnterior();

    Map<String, Object> obtenerComparacionVentas();

    @Transactional
    PurchaseDTO confirmPurchase(Integer purchaseId);

    PurchaseDTO getPurchaseById(Integer id);

    @Transactional(readOnly = true)
    List<PurchaseDTO> getPurchaseHistoryByUserId();


    PurchaseDTO updateShipmentStatus(Integer purchaseId, ShipmentStatus shipmentStatus);
}
