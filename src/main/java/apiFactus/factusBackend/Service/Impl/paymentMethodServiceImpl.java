package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Dto.payment_methodRequestDTO;
import apiFactus.factusBackend.Dto.payment_methodResponseDTO;
import apiFactus.factusBackend.Domain.Entity.payment_method;
import apiFactus.factusBackend.Repository.metodoPagoRepository;
import apiFactus.factusBackend.Service.paymentMethodService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class paymentMethodServiceImpl implements paymentMethodService {


private  final metodoPagoRepository metodoPagoRepository;
    public paymentMethodServiceImpl(metodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Override
    public List<payment_methodResponseDTO> listarMetodoDePago() {
        return metodoPagoRepository.findAll().stream()
                .map(methodsPago -> new payment_methodResponseDTO(
                        methodsPago.getCodigo(),
                        methodsPago.getNombre()
                                ))
                .collect(Collectors.toList());
    }
    @Override
    public payment_methodRequestDTO crearMetodoPago(payment_methodRequestDTO paymentMethodRequestDTO) {
        metodoPagoRepository.findByNombre(paymentMethodRequestDTO.getNombre())
                .ifPresent(existeNombre -> {
                    throw new RuntimeException("El nombre del pago ya existe");
                });
        payment_method nuevoMetodoPago = new payment_method();
        nuevoMetodoPago.setCodigo(paymentMethodRequestDTO.getCodigo());
        nuevoMetodoPago.setNombre(paymentMethodRequestDTO.getNombre());
        metodoPagoRepository.save(nuevoMetodoPago);
        return new payment_methodRequestDTO(nuevoMetodoPago.getId(), nuevoMetodoPago.getCodigo(), nuevoMetodoPago.getNombre());
    }



}
