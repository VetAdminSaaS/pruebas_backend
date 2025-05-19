package apiFactus.factusBackend.Mapper;

import apiFactus.factusBackend.Domain.Entity.*;
import apiFactus.factusBackend.Dto.*;
import apiFactus.factusBackend.Repository.ProductoRepository;
import apiFactus.factusBackend.exception.ProductNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PurchaseMapper {
    private final ModelMapper modelMapper;
    private final ProductoRepository productoRepository;

    public PurchaseMapper(ModelMapper modelMapper, ProductoRepository productoRepository) {
        this.modelMapper = modelMapper;
        this.productoRepository = productoRepository;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Purchase toPurchaseEntity(PurchaseCreateUpadteDTO purchaseDTO) {
        Purchase purchase = modelMapper.map(purchaseDTO, Purchase.class);

        if (purchaseDTO.getCustomer() != null) {
            Usuario usuario = new Usuario();
            purchase.setUser(usuario);
        }

        purchase.setItems(purchaseDTO.getItems().stream()
                .map(this::toPurchaseItemEntity)
                .toList());
        return purchase;
    }

    public PurchaseDTO toPurchaseDTO(Purchase purchase) {
        PurchaseDTO purchaseDTO = modelMapper.map(purchase, PurchaseDTO.class);

        // 🔹 Mapear los items
        purchaseDTO.setItems(purchase.getItems().stream()
                .map(this::toPurchaseItemDTO)
                .toList());

        // 🔹 Incluir el customer desde el usuario
        if (purchase.getUser() != null && purchase.getUser().getCustomer() != null) {
            Customer customer = purchase.getUser().getCustomer();
            purchaseDTO.setCustomer(modelMapper.map(customer, CustomerDTO.class));

            // Obtener el nombre directamente desde Customer
            purchaseDTO.setNames(customer.getNames());
        } else {
            purchaseDTO.setNames(null); // O alguna lógica alternativa
        }

        return purchaseDTO;
    }



    private PurchaseItem toPurchaseItemEntity(PurchaseItemCreateUpdateDTO purchaseItemDTO) {
        // Mapea el DTO a la entidad PurchaseItem
        PurchaseItem purchaseItemEntity = modelMapper.map(purchaseItemDTO, PurchaseItem.class);

        // Verifica si el DTO tiene un ID de producto válido
        if (purchaseItemDTO.getProductoId() != null && purchaseItemDTO.getProductoId() > 0) {
            productos_Tienda productoEntity = productoRepository.findById(purchaseItemDTO.getProductoId())
                    .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con ID: " + purchaseItemDTO.getProductoId()));

            // Asigna el producto encontrado
            purchaseItemEntity.setProducto(productoEntity);
        }

        return purchaseItemEntity;
    }









    private PurchaseItemDTO toPurchaseItemDTO(PurchaseItem purchaseItem) {
        PurchaseItemDTO purchaseItemDTO = modelMapper.map(purchaseItem, PurchaseItemDTO.class);
        purchaseItemDTO.setName(purchaseItem.getProducto().getName());
        purchaseItemDTO.setCodeReference(purchaseItem.getProducto().getCodeReference());
        purchaseItemDTO.setDiscountRate(purchaseItem.getProducto().getDiscountRate());
        purchaseItemDTO.setPrice(purchaseItem.getProducto().getPrice());
        purchaseItemDTO.setStandardCodeId(purchaseItem.getProducto().getStandardCodeId());
        purchaseItemDTO.setTributeId(purchaseItem.getProducto().getTributeId());
        purchaseItemDTO.setWithholdingTaxes(purchaseItem.getProducto().getWithholdingTaxes());
        purchaseItemDTO.setTaxRate(purchaseItem.getProducto().getTaxRate());
        purchaseItemDTO.setUnitMeasureId(purchaseItem.getProducto().getUnitMeasureId());
        purchaseItemDTO.setIsExcluded(purchaseItem.getProducto().getIsExcluded());
        return purchaseItemDTO;
    }
}
