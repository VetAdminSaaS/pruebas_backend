package apiFactus.factusBackend.Service;

import apiFactus.factusBackend.Dto.payment_methodRequestDTO;
import apiFactus.factusBackend.Dto.payment_methodResponseDTO;

import java.util.List;

public interface paymentMethodService {
    List<payment_methodResponseDTO> listarMetodoDePago();

    payment_methodRequestDTO crearMetodoPago(payment_methodRequestDTO paymentMethodRequestDTO);
}
