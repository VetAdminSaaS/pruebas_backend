package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Dto.SucursalesDTO;
import apiFactus.factusBackend.Dto.SucursalesDetailsDTO;
import apiFactus.factusBackend.Repository.SucursalesRepository;
import apiFactus.factusBackend.Service.SucursalesService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sucursales")
public class SucursalesController {

    private final SucursalesService sucursalesService;
    private final SucursalesRepository sucursalesRepository;

    @GetMapping
    public ResponseEntity<List<SucursalesDetailsDTO>> getAllSucursales() {
        List<SucursalesDetailsDTO> sucursalesDTO = sucursalesService.getAllSucursales();
        return new ResponseEntity<>(sucursalesDTO, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<SucursalesDetailsDTO> getSucursalesById(@PathVariable Long id) {
        SucursalesDetailsDTO sucursalesDTO = sucursalesService.findById(id);
        return new ResponseEntity<>(sucursalesDTO, HttpStatus.OK);
    }
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalesDTO> crearSucursal(@RequestBody SucursalesDTO sucursalesDTO) throws BadRequestException {
        SucursalesDTO sucursalesDTOcreated = sucursalesService.create(sucursalesDTO);
        return new ResponseEntity<>(sucursalesDTOcreated, HttpStatus.CREATED);

    }
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalesDTO> updateSucursal(@RequestBody SucursalesDTO sucursalesDTO, @PathVariable Long id) {
        SucursalesDTO sucursalesDTOupdate = sucursalesService.update(id, sucursalesDTO);
        return new ResponseEntity<>(sucursalesDTOupdate, HttpStatus.OK);
    }
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalesDTO> eliminarSucursal(@PathVariable Long id) {
        sucursalesService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
