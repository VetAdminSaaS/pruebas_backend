package apiFactus.factusBackend.Controller;

import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import apiFactus.factusBackend.Dto.*;
import apiFactus.factusBackend.Mapper.ProductoMapper;
import apiFactus.factusBackend.Repository.ProductoRepository;
import apiFactus.factusBackend.Service.ProductoService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class AdminProductoController {
    private final ProductoService productoService;
    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDetailsDTO> createProducto(@RequestBody productoCreateDTO productoDTO) throws BadRequestException {
        ProductoDetailsDTO crearProducto = productoService.createProducto(productoDTO);
        return new ResponseEntity<>(crearProducto, HttpStatus.CREATED);
    }


    @GetMapping("/listar")

    public ResponseEntity<List<ProductoDetailsDTO>> listarProductos() {
        List<ProductoDetailsDTO> productos = productoService.getAllProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDetailsDTO> get(@PathVariable Integer id) {
        ProductoDetailsDTO producto = productoService.findById(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }
    @GetMapping("/total")
    public ResponseEntity<Long> getTotalProducts() {
        long totalProducts = productoService.getTotalProducts();
        return ResponseEntity.ok(totalProducts);
    }
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ProductoDetailsDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody productoCreateDTO productoFormDto) throws BadRequestException {
        ProductoDetailsDTO actualizarProducto = productoService.update(id, productoFormDto);
        return new ResponseEntity<>(actualizarProducto, HttpStatus.OK);
    }
    @GetMapping("/page")
    public ResponseEntity<Page<ProductoDetailsDTO>> paginate(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ProductoDetailsDTO> page = productoService.paginate(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    @GetMapping("/{id}/tipos-entrega")
    public TipoEntregaResponse getTiposEntrega(@PathVariable Long id) {
        return productoService.getTiposEntregaPorProducto(id);
    }
    @GetMapping("/{productoId}/sucursales-con-stock")
    public ResponseEntity<SucursalProductoResponseDTO> getSucursalesConStock(@PathVariable Long productoId) {
        SucursalProductoResponseDTO response = productoService.getSucursalesPorProducto(productoId);

        if (response.getSucursales().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductoDetailsDTO>> getCategorias(@PathVariable Integer categoryId) {
        List<ProductoDetailsDTO> productos = productoService.obtenerProductosPorCategory(categoryId);
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
    @GetMapping("/nombre/{name}")
    public ResponseEntity<List<productos_Tienda>> getNombre(@PathVariable(name = "name", required = false) String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<productos_Tienda> productos = productoService.obtenerProductoPorNombre(name);

        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(productos);
    }




}
