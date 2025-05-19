package apiFactus.factusBackend.Service.Impl;

import apiFactus.factusBackend.Domain.Entity.Categoria;
import apiFactus.factusBackend.Domain.Entity.SucursalProducto;
import apiFactus.factusBackend.Domain.Entity.Sucursales;
import apiFactus.factusBackend.Domain.enums.TipoEntrega;
import apiFactus.factusBackend.Dto.*;
import apiFactus.factusBackend.Domain.Entity.productos_Tienda;
import apiFactus.factusBackend.Mapper.ProductoMapper;
import apiFactus.factusBackend.Repository.CategoriaRepository;
import apiFactus.factusBackend.Repository.ProductoRepository;
import apiFactus.factusBackend.Repository.SucursalProductoRepository;
import apiFactus.factusBackend.Repository.SucursalesRepository;
import apiFactus.factusBackend.Service.ProductoService;
import apiFactus.factusBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class productoServiceImpl implements ProductoService {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SucursalesRepository sucursalesRepository;
    private final SucursalProductoRepository sucursalProductoRepository;
    private static final Logger log = LoggerFactory.getLogger(productoServiceImpl.class);

    @Override
    public ProductoDetailsDTO createProducto(productoCreateDTO productoCreateDTO) throws BadRequestException {
        Categoria categoria = categoriaRepository.findById(productoCreateDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el ID: " + productoCreateDTO.getCategoryId()));

        productos_Tienda producto = productoMapper.toEntity(productoCreateDTO);
        producto.setCreatedAt(LocalDateTime.now());
        producto.setCategoria(categoria);

        List<TipoEntrega> tiposEntrega = Optional.ofNullable(productoCreateDTO.getTiposEntrega())
                .orElse(Collections.emptyList());

        producto.setTiposEntrega(tiposEntrega);

        if (tiposEntrega.contains(TipoEntrega.DESPACHO_A_DOMICILIO)) {
            if (productoCreateDTO.getCostoDespacho() == null || productoCreateDTO.getCostoDespacho() < 0) {
                throw new BadRequestException("Costo de despacho debe ser mayor o igual a 0 cuando el tipo de entrega es DESPACHO_A_DOMICILIO");
            }
            producto.setCostoDespacho(productoCreateDTO.getCostoDespacho());
        } else {
            producto.setCostoDespacho(0.0);
        }

        producto = productoRepository.save(producto);
        List<SucursalProducto> sucursalProductos = new ArrayList<>();

        for (SucursalStockDTO sucursalStockDTO : Optional.ofNullable(productoCreateDTO.getSucursalesStock()).orElse(Collections.emptyList())) {
            Sucursales sucursal = sucursalesRepository.findById(sucursalStockDTO.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con el ID: " + sucursalStockDTO.getSucursalId()));
            SucursalProducto sucursalProducto = new SucursalProducto();
            sucursalProducto.setProducto(producto);
            sucursalProducto.setSucursal(sucursal);
            sucursalProducto.setQuantity(sucursalStockDTO.getQuantity());

            sucursalProductos.add(sucursalProducto);
        }
        if (!sucursalProductos.isEmpty()) {
            sucursalProductoRepository.saveAll(sucursalProductos);
        }
        producto.setSucursalesStock(sucursalProductos);
        return productoMapper.toDetailsDTO(producto);
    }


    @Override
    public List<ProductoDetailsDTO> getAllProductos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toDetailsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoDetailsDTO findById(Integer id) {
        productos_Tienda producto = productoRepository.findByIdWithSucursalesStock(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        ProductoDetailsDTO productoDTO = productoMapper.toDetailsDTO(producto);

        List<SucursalStockDTO> sucursalesStock = producto.getSucursalesStock().stream()
                .map(sp -> new SucursalStockDTO(sp.getSucursal().getId(), sp.getQuantity()))
                .collect(Collectors.toList());

        productoDTO.setSucursalesStock(sucursalesStock);

        productoDTO.setTiposEntrega(producto.getTiposEntrega());
        productoDTO.setCostoDespacho(producto.getCostoDespacho());

        return productoDTO;
    }
    @Transactional
    @Override
    public Page<ProductoDetailsDTO> paginate(Pageable pageable) {
        return productoRepository.findAll(pageable)
                .map(productoMapper::toDetailsDTO);
    }


    @Override
    public long getTotalProducts() {
        return productoRepository.count();
    }

    @Transactional
    @Override
    public ProductoDetailsDTO update(Integer id, productoCreateDTO productoUpdate) throws BadRequestException {


        // Buscar el producto en la base de datos
        productos_Tienda productoFromDb = productoRepository.findById(id.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Buscar la categoría
        Categoria categoria = categoriaRepository.findById(productoUpdate.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // Actualizar datos del producto
        productoFromDb.setName(productoUpdate.getName());
        productoFromDb.setCodeReference(productoUpdate.getCodeReference());
        productoFromDb.setPrice(productoUpdate.getPrice());
        productoFromDb.setDiscountRate(productoUpdate.getDiscountRate());
        productoFromDb.setTaxRate(productoUpdate.getTaxRate());
        productoFromDb.setUnitMeasureId(productoUpdate.getUnitMeasureId());
        productoFromDb.setCoverPath(productoUpdate.getCoverPath());
        productoFromDb.setFilePath(productoUpdate.getFilePath());
        productoFromDb.setStandardCodeId(productoUpdate.getStandardCodeId());
        productoFromDb.setIsExcluded(productoUpdate.getIsExcluded());
        productoFromDb.setTributeId(productoUpdate.getTributeId());
        productoFromDb.setWithholdingTaxes(productoUpdate.getWithholdingTaxes());
        productoFromDb.setUpdatedAt(LocalDateTime.now());
        productoFromDb.setDescripcion(productoUpdate.getDescripcion());
        productoFromDb.setCategoria(categoria);
        productoFromDb.setTiposEntrega(productoUpdate.getTiposEntrega());


        if (!productoUpdate.getTiposEntrega().isEmpty() &&
                productoUpdate.getTiposEntrega().get(0) == TipoEntrega.DESPACHO_A_DOMICILIO) {
            productoFromDb.setCostoDespacho(productoUpdate.getCostoDespacho());
        } else {
            productoFromDb.setCostoDespacho(null);
        }


        // Obtener las sucursales del DTO (manejo seguro de nulos)
        List<SucursalStockDTO> sucursalesDTO = Optional.ofNullable(productoUpdate.getSucursalesStock())
                .orElse(Collections.emptyList());

        log.info("Sucursales recibidas: {}", sucursalesDTO);

        // Obtener los IDs de sucursales
        Set<Long> sucursalIds = sucursalesDTO.stream()
                .map(SucursalStockDTO::getSucursalId)
                .filter(Objects::nonNull) // Evita IDs nulos
                .collect(Collectors.toSet());

        if (sucursalIds.isEmpty() && !sucursalesDTO.isEmpty()) {
            throw new BadRequestException("Algunas sucursales tienen un ID nulo.");
        }

        // Buscar sucursales en la BD
        List<Sucursales> sucursales = sucursalesRepository.findAllById(sucursalIds);
        Set<Long> sucursalesEncontradasIds = sucursales.stream()
                .map(Sucursales::getId)
                .collect(Collectors.toSet());

        // Verificar si alguna sucursal no fue encontrada
        Set<Long> sucursalesNoEncontradas = new HashSet<>(sucursalIds);
        sucursalesNoEncontradas.removeAll(sucursalesEncontradasIds);
        if (!sucursalesNoEncontradas.isEmpty()) {
            throw new ResourceNotFoundException("Las siguientes sucursales no existen: " + sucursalesNoEncontradas);
        }

        // Obtener relaciones actuales producto-sucursal
        List<SucursalProducto> sucursalesActuales = sucursalProductoRepository.findByProducto(productoFromDb);
        Map<Long, SucursalProducto> sucursalProductoMap = sucursalesActuales.stream()
                .collect(Collectors.toMap(sp -> sp.getSucursal().getId(), sp -> sp));

        // Lista de relaciones a actualizar o crear
        List<SucursalProducto> sucursalesAActualizar = new ArrayList<>();

        for (SucursalStockDTO sucursalStockDTO : sucursalesDTO) {
            Sucursales sucursal = sucursales.stream()
                    .filter(s -> s.getId().equals(sucursalStockDTO.getSucursalId()))
                    .findFirst()
                    .orElse(null);

            if (sucursal != null) {
                SucursalProducto sucursalProducto = sucursalProductoMap.getOrDefault(sucursal.getId(), new SucursalProducto());
                sucursalProducto.setProducto(productoFromDb);
                sucursalProducto.setSucursal(sucursal);
                sucursalProducto.setQuantity(sucursalStockDTO.getQuantity());
                sucursalesAActualizar.add(sucursalProducto);
            }
        }

        // Determinar relaciones a eliminar (sucursales que ya no están en la lista)
        Set<Long> nuevasSucursalesIds = sucursalesDTO.stream()
                .map(SucursalStockDTO::getSucursalId)
                .collect(Collectors.toSet());

        List<SucursalProducto> sucursalesAEliminar = sucursalesActuales.stream()
                .filter(sp -> !nuevasSucursalesIds.contains(sp.getSucursal().getId()))
                .collect(Collectors.toList());

        // Eliminar relaciones antiguas si es necesario
        if (!sucursalesAEliminar.isEmpty()) {
            sucursalProductoRepository.deleteAll(sucursalesAEliminar);
        }

        // Guardar relaciones nuevas o actualizadas
        if (!sucursalesAActualizar.isEmpty()) {
            sucursalProductoRepository.saveAll(sucursalesAActualizar);
        }

        // Guardar cambios en el producto
        productos_Tienda productoGuardado = productoRepository.save(productoFromDb);

        // Convertir a DTO y devolver el resultado
        ProductoDetailsDTO productoDetailsDTO = productoMapper.toDetailsDTO(productoGuardado);

        productoDetailsDTO.setSucursalesStock(sucursalesAActualizar.stream().map(sp -> new SucursalStockDTO(
                sp.getSucursal().getId(),
                sp.getQuantity()
        )).collect(Collectors.toList()));

        return productoDetailsDTO;
    }


    @Transactional
    @Override
    public void delete(Integer id) {
        productos_Tienda productos = productoRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + id));
        productoRepository.delete(productos);
    }

    @Override
    public TipoEntregaResponse getTiposEntregaPorProducto(Long productoId) {
        List<TipoEntrega> tiposEntrega = productoRepository.findTiposEntregaByProductoId(productoId);

        // Obtener el costo de despacho desde productos_tienda
        Double costoDespacho = productoRepository.findCostoDespachoByProductoId(productoId)
                .orElse(0.0); // Si no hay valor, asignamos 0.0

        return new TipoEntregaResponse(productoId, tiposEntrega, costoDespacho);
    }

    @Override
    public SucursalProductoResponseDTO getSucursalesPorProducto(Long productoId) {
        List<SucursalStockDTO> sucursalesDTO = productoRepository.findSucursalesConStock(productoId)
                .stream()
                .map(sp -> new SucursalStockDTO(sp.getSucursal().getId(), sp.getSucursal().getNombre(), sp.getQuantity()))
                .collect(Collectors.toList());

        return new SucursalProductoResponseDTO(productoId, sucursalesDTO);
    }

    @Override
    public List<ProductoDetailsDTO> obtenerProductosPorCategory(Integer categoryId) {
        List<productos_Tienda> productos = productoRepository.findByCategoriaId(categoryId);


        return productos.stream().map(productoMapper::toDetailsDTO).collect(Collectors.toList());
    }
    @Override
    public List<productos_Tienda> obtenerProductoPorNombre(String name){
        return productoRepository.findByNameContaining(name);
    }
}
