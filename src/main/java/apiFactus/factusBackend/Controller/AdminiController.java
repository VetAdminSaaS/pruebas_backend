package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.payment_methodRequestDTO;
import apiFactus.factusBackend.Dto.payment_methodResponseDTO;
import apiFactus.factusBackend.Service.paymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminiController {
private final paymentMethodService metodoPagoService;

    @PostMapping("/create")
    public ResponseEntity<payment_methodRequestDTO> crearMetodoPago(@Valid @RequestBody payment_methodRequestDTO paymentMethodRequestDTO){
        payment_methodRequestDTO paymentMethodDTO = metodoPagoService.crearMetodoPago(paymentMethodRequestDTO);
        return new ResponseEntity<>(paymentMethodDTO, HttpStatus.CREATED);
    }
    @GetMapping("/listar")
    public ResponseEntity<List<payment_methodResponseDTO>> getMetodoPago(){
        List<payment_methodResponseDTO> metodoPagoList = metodoPagoService.listarMetodoDePago();
        return  new ResponseEntity<>(metodoPagoList, HttpStatus.OK);
    }
}
