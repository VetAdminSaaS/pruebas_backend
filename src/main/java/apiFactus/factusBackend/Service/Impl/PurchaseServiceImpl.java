package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Purchase;
import apiFactus.factusBackend.Domain.Entity.PurchaseItem;
import apiFactus.factusBackend.Domain.Entity.Usuario;
import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import apiFactus.factusBackend.Domain.enums.PaymentStatus;
import apiFactus.factusBackend.Domain.enums.ShipmentStatus;
import apiFactus.factusBackend.Dto.PurchaseCreateUpadteDTO;
import apiFactus.factusBackend.Dto.PurchaseDTO;
import apiFactus.factusBackend.Mapper.PurchaseMapper;
import apiFactus.factusBackend.Repository.ProductoRepository;
import apiFactus.factusBackend.Repository.PurchaseRepository;
import apiFactus.factusBackend.Repository.UsuarioRepository;
import apiFactus.factusBackend.Service.PurchaseService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import apiFactus.factusBackend.integration.factus.halltec.Dto.rangoNumericoDTO;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturaService;
import apiFactus.factusBackend.integration.factus.halltec.Service.FacturacionDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ProductoRepository productoRepository;
    private final PurchaseMapper purchaseMapper;
    private final UsuarioRepository usuarioRepository;
    private final FacturaService factusService;
    private final FacturacionDataService facturacionDataService;

    @Override
    @Transactional
    public PurchaseDTO createPurchase(PurchaseCreateUpadteDTO purchaseDTO) {
        Purchase purchase = purchaseMapper.toPurchaseEntity(purchaseDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = null;

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            usuario = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("❌ Usuario no encontrado."));
        }

        if (usuario == null || usuario.getCustomer() == null) {
            throw new RuntimeException("❌ El usuario no tiene un cliente asociado.");
        }

        purchase.setUser(usuario);

        if (purchaseDTO.getNumberingRangeId() != null) {
            purchase.setNumberingRangeId(purchaseDTO.getNumberingRangeId());
        } else {
            List<rangoNumericoDTO> listaRangos = facturacionDataService.obtenerRangoNumerico();
            if (listaRangos == null || listaRangos.isEmpty()) {
                throw new RuntimeException("❌ No se encontró ningún rango de numeración activo.");
            }
            purchase.setNumberingRangeId(listaRangos.get(0).getId());
        }
        if(purchaseDTO.getNumber() !=null){
            purchase.setNumber(purchaseDTO.getNumber());
        }

        for (PurchaseItem item : purchase.getItems()) {
            if (item.getProducto() == null || item.getProducto().getId() == null) {
                throw new IllegalArgumentException("❌ Producto inválido en la compra.");
            }

            productos_Tienda producto = productoRepository.findById(item.getProducto().getId().longValue())
                    .orElseThrow(() -> new RuntimeException("❌ Producto no encontrado con ID: " + item.getProducto().getId()));

            item.setProducto(producto);
            item.setPurchase(purchase);
        }
        purchase.setCreatedAt(LocalDateTime.now());
        purchase.setPaymentStatus(PaymentStatus.PENDING);
        purchase.setTotal((float) purchase.getItems()
                .stream()
                .mapToDouble(item -> (item.getPrice() - (item.getPrice() * item.getProducto().getDiscountRate() / 100)) * item.getQuantity())
                .sum());
        Purchase savedPurchase = purchaseRepository.save(purchase);

        System.out.println("✅ Compra guardada con éxito - ID: " + savedPurchase.getId() + ", Número de Rango: " + savedPurchase.getNumberingRangeId());

        return purchaseMapper.toPurchaseDTO(savedPurchase);
    }



    @Override
    public List<PurchaseDTO> getAllPurchases() {
        return purchaseRepository.findByPaymentStatus(PaymentStatus.PAID)  // Obtener solo las pagadas desde la base de datos
                .stream()
                .map(purchaseMapper::toPurchaseDTO)
                .collect(Collectors.toList());
    }
    @Override
    public double getTotalPaidPurchases() {
        Double total = purchaseRepository.sumTotalByPaymentStatus(PaymentStatus.PAID);
        return total != null ? total : 0.0;
    }
    @Override
    public Long contarComprasConNumero() {
        return purchaseRepository.countByNumberNotNull();
    }
    @Override
    public Double obtenerTotalVentasMesActual() {
        return purchaseRepository.sumTotalSalesCurrentMonthNative();
    }
    @Override
    public Map<String, Double> obtenerVentasMesActualYAnterior() {
        Double ventasMesActual = purchaseRepository.sumTotalSalesCurrentMonthNative();
        Double ventasMesAnterior = purchaseRepository.sumTotalSalesPreviousMonthNative();

        Map<String, Double> ventas = new HashMap<>();
        ventas.put("actual", ventasMesActual);
        ventas.put("anterior", ventasMesAnterior);

        return ventas;
    }

    @Override
    public Map<String, Object> obtenerComparacionVentas() {
        List<Object[]> result = purchaseRepository.getSalesComparison();
        Double totalActual = ((Number) result.get(0)[0]).doubleValue();
        Double totalAnterior = ((Number) result.get(0)[1]).doubleValue();

        // Cálculo del porcentaje de crecimiento
        Double porcentajeCrecimiento = (totalAnterior == 0) ? 100.0 : ((totalActual - totalAnterior) / totalAnterior) * 100;

        // Devolvemos los datos en un mapa
        Map<String, Object> response = new HashMap<>();
        response.put("total_actual", totalActual);
        response.put("total_anterior", totalAnterior);
        response.put("porcentaje_crecimiento", porcentajeCrecimiento);

        return response;
    }



    @Transactional
    @Override
    public PurchaseDTO confirmPurchase(Integer purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(()-> new ResourceNotFoundException("Purchase not found"));

        purchase.setPaymentStatus(PaymentStatus.PAID);

        Purchase updatedPurchase = purchaseRepository.save(purchase);
        return purchaseMapper.toPurchaseDTO(updatedPurchase);
    }
    @Override
    public PurchaseDTO getPurchaseById(Integer id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Purchase not found"));
        return purchaseMapper.toPurchaseDTO(purchase);
    }
    @Transactional(readOnly = true)
    @Override
    public List<PurchaseDTO> getPurchaseHistoryByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = null;
        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            usuario = usuarioRepository.findByEmail(authentication.getName())
                    .orElseThrow(ResourceNotFoundException::new);
        }
        return purchaseRepository.findByUserId(usuario.getId()).stream()
                .map(purchaseMapper::toPurchaseDTO)
                .toList();
    }

    @Override
    public PurchaseDTO updateShipmentStatus(Integer purchaseId, ShipmentStatus shipmentStatus) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        purchase.setShipmentStatus(shipmentStatus);
        purchaseRepository.save(purchase);

        return purchaseMapper.toPurchaseDTO(purchase);
    }


}
