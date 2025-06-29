package apiFactus.factusBackend.controller;

import apiFactus.factusBackend.Controller.AdminiController;
import apiFactus.factusBackend.Dto.payment_methodRequestDTO;
import apiFactus.factusBackend.Dto.payment_methodResponseDTO;
import apiFactus.factusBackend.Service.paymentMethodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminiControllerTest {

    @Mock
    private paymentMethodService metodoPagoService;

    @InjectMocks
    private AdminiController adminiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearMetodoPago_debeRetornarResponseEntityCreated() {
        // Arrange
        payment_methodRequestDTO requestDTO = new payment_methodRequestDTO();
        requestDTO.setNombre("Yape");
        requestDTO.setCodigo("1234");

        when(metodoPagoService.crearMetodoPago(any())).thenReturn(requestDTO);

        // Act
        ResponseEntity<payment_methodRequestDTO> response = adminiController.crearMetodoPago(requestDTO);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Yape", response.getBody().getNombre());
        assertEquals("1234", response.getBody().getCodigo());
        verify(metodoPagoService, times(1)).crearMetodoPago(requestDTO);
    }

    @Test
    void testGetMetodoPago_debeRetornarListaDeMetodosConOK() {
        // Arrange
        payment_methodResponseDTO dto1 = new payment_methodResponseDTO("1234", "Yape");
        payment_methodResponseDTO dto2 = new payment_methodResponseDTO("5678", "Transferencia");
        List<payment_methodResponseDTO> listaEsperada = Arrays.asList(dto1, dto2);

        when(metodoPagoService.listarMetodoDePago()).thenReturn(listaEsperada);

        // Act
        ResponseEntity<List<payment_methodResponseDTO>> response = adminiController.getMetodoPago();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Yape", response.getBody().get(0).getNombre());
        assertEquals("Transferencia", response.getBody().get(1).getNombre());
        verify(metodoPagoService, times(1)).listarMetodoDePago();
    }
}
