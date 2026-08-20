package etctech.gestionventas.service.impl;

import etctech.gestionventas.dto.request.DetallePedidoRequestDTO;
import etctech.gestionventas.dto.request.PedidoFiltroDTO;
import etctech.gestionventas.dto.request.PedidoRequestDTO;
import etctech.gestionventas.dto.response.*;
import etctech.gestionventas.entity.*;
import etctech.gestionventas.exception.*;
import etctech.gestionventas.repository.ClienteRepository;
//import etctech.gestionventas.repository.DetallePedidoRepository;
import etctech.gestionventas.repository.PedidoRepository;
import etctech.gestionventas.repository.ProductoRepository;
import etctech.gestionventas.service.PedidoService;
import etctech.gestionventas.specification.PedidoSpecification;
import etctech.gestionventas.util.Constants;
import etctech.gestionventas.util.EstadoPedidoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
   // private final DetallePedidoRepository detallePedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(
            rollbackFor = {Exception.class, BusinessException.class},
            timeout = 30
    )
    public PedidoResponseDTO registrarPedido(PedidoRequestDTO request) {
        log.info("Registrando nuevo pedido para cliente ID: {}", request.getIdCliente());

        try {
            // 1. Validar que el cliente existe y está activo
            Cliente cliente = clienteRepository.findByIdClienteAndEstadoTrue(request.getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getIdCliente()));

            // 2. Validar que hay detalles
            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                throw new BusinessException("El pedido debe tener al menos un detalle");
            }

            // 3. Crear el pedido
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setFechaPedido(LocalDateTime.now());
            pedido.setEstado(Constants.ESTADO_PENDIENTE);

            // 4. Procesar cada detalle
            BigDecimal total = BigDecimal.ZERO;
            for (DetallePedidoRequestDTO detalleRequest : request.getDetalles()) {
                // 4.1 Validar producto
                Producto producto = productoRepository.findByIdProductoAndEstadoTrue(detalleRequest.getIdProducto())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalleRequest.getIdProducto()));

                // 4.2 RN-04: No se pueden vender productos inactivos
                if (!producto.isActivo()) {
                    throw new InactiveProductException(producto.getIdProducto(), producto.getNombre());
                }

                // 4.3 RN-01: Validar stock suficiente
                if (!producto.tieneStockSuficiente(detalleRequest.getCantidad())) {
                    throw new InsufficientStockException(
                            producto.getIdProducto(),
                            producto.getNombre(),
                            producto.getStock(),
                            detalleRequest.getCantidad()
                    );
                }

                // 4.4 Crear detalle
                DetallePedido detalle = DetallePedido.fromProducto(producto, detalleRequest.getCantidad());
                detalle.setPedido(pedido);

                // 4.5 RN-02: Descontar stock
                producto.descontarStock(detalleRequest.getCantidad());

                pedido.getDetalles().add(detalle);
                total = total.add(detalle.getSubtotal());
            }

            // 5. Establecer el total
            pedido.setTotal(total);

            // 6. Cambiar estado a CONFIRMADO (ya se descontó stock)
            pedido.setEstado(Constants.ESTADO_CONFIRMADO);

            // 7. Guardar todo en una transacción
            Pedido saved = pedidoRepository.save(pedido);

            // Forzar flush para asegurar que los cambios se persistan inmediatamente
            pedidoRepository.flush();

            log.info("Pedido registrado exitosamente con ID: {}, Total: {}", saved.getIdPedido(), saved.getTotal());

            return mapToResponseDTO(saved);

        } catch (Exception e) {
            log.error("Error al registrar pedido: {}", e.getMessage(), e);
            throw e; // La transacción hará rollback automáticamente
        }
    }
    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPedidoPorId(Long id) {
        log.info("Obteniendo pedido con ID: {}", id);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        return mapToResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidos(Pageable pageable) {
        log.info("Listando pedidos con paginación");

        // Excluir pedidos anulados por defecto
        Specification<Pedido> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("estado"), Constants.ESTADO_ANULADO);

        return pedidoRepository.findAll(spec, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidosPorCliente(Long idCliente, Pageable pageable) {
        log.info("Listando pedidos del cliente ID: {}", idCliente);

        // Verificar que el cliente existe
        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return pedidoRepository.findByClienteIdClienteAndEstadoNot(idCliente, Constants.ESTADO_ANULADO, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> buscarPedidosConFiltros(PedidoFiltroDTO filtro, Pageable pageable) {
        log.info("Buscando pedidos con filtros: {}", filtro);

        Specification<Pedido> spec = PedidoSpecification.filtrarPedidos(filtro);
        return pedidoRepository.findAll(spec, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstadoPedido(Long id, String nuevoEstado) {
        log.info("Cambiando estado del pedido ID: {} a {}", id, nuevoEstado);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        // Validar que el nuevo estado sea válido
        if (!Constants.ESTADO_PENDIENTE.equals(nuevoEstado) &&
                !Constants.ESTADO_CONFIRMADO.equals(nuevoEstado) &&
                !Constants.ESTADO_ENVIADO.equals(nuevoEstado) &&
                !Constants.ESTADO_ENTREGADO.equals(nuevoEstado) &&
                !Constants.ESTADO_ANULADO.equals(nuevoEstado)) {
            throw new BusinessException("Estado inválido: " + nuevoEstado);
        }

        // Validar transición permitida
        if (!EstadoPedidoValidator.isTransitionValid(pedido.getEstado(), nuevoEstado)) {
            throw new InvalidStateTransitionException(pedido.getEstado(), nuevoEstado);
        }

        // Casos especiales al cambiar a ANULADO
        if (Constants.ESTADO_ANULADO.equals(nuevoEstado)) {
            // Si el pedido está confirmado, devolver stock
            if (pedido.isConfirmado()) {
                devolverStock(pedido);
            }
        }

        pedido.setEstado(nuevoEstado);
        Pedido updated = pedidoRepository.save(pedido);
        log.info("Estado del pedido ID: {} actualizado a {}", id, nuevoEstado);

        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class, BusinessException.class},
            timeout = 30
    )
    public void anularPedido(Long id) {
        log.info("Anulando pedido con ID: {}", id);

        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

            // Verificar que el pedido no esté ya anulado
            if (pedido.isAnulado()) {
                throw new BusinessException("El pedido ya se encuentra anulado");
            }

            // RN-03: Devolver stock si el pedido está confirmado o en estados posteriores
            if (pedido.isConfirmado() || pedido.getEstado().equals(Constants.ESTADO_ENVIADO)) {
                devolverStock(pedido);
            }

            // Actualizar estado
            pedido.setEstado(Constants.ESTADO_ANULADO);
            pedidoRepository.save(pedido);

            // Forzar flush para asegurar que los cambios se persistan
            pedidoRepository.flush();

            log.info("Pedido anulado exitosamente con ID: {}", id);

        } catch (Exception e) {
            log.error("Error al anular pedido: {}", e.getMessage(), e);
            throw e; // La transacción hará rollback automáticamente
        }
    }



    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo pedidos entre {} y {}", fechaInicio, fechaFin);

        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("La fecha de inicio y fin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> obtenerPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        log.info("Obteniendo pedidos entre {} y {} con paginación", fechaInicio, fechaFin);

        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("La fecha de inicio y fin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        return pedidoRepository.findByFechaPedidoBetween(fechaInicio, fechaFin, pageable)
                .map(this::mapToResponseDTO);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * RN-03: Devolver stock al anular un pedido
     */
    private void devolverStock(Pedido pedido) {
        log.info("Devolviendo stock para pedido ID: {}", pedido.getIdPedido());

        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.aumentarStock(detalle.getCantidad());
            log.info("Stock devuelto: Producto '{}' +{} unidades",
                    producto.getNombre(), detalle.getCantidad());
        }
    }

    private PedidoResponseDTO mapToResponseDTO(Pedido pedido) {
        return PedidoResponseDTO.builder()
                .idPedido(pedido.getIdPedido())
                .cliente(mapToClienteResponseDTO(pedido.getCliente()))
                .fechaPedido(pedido.getFechaPedido())
                .total(pedido.getTotal())
                .estado(pedido.getEstado())
                .detalles(pedido.getDetalles().stream()
                        .map(this::mapToDetalleResponseDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private ClienteResponseDTO mapToClienteResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .idCliente(cliente.getIdCliente())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .estado(cliente.getEstado())
                .fechaRegistro(cliente.getFechaRegistro())
                .nombreCompleto(cliente.getNombreCompleto())
                .build();
    }

    private DetallePedidoResponseDTO mapToDetalleResponseDTO(DetallePedido detalle) {
        return DetallePedidoResponseDTO.builder()
                .idDetallePedido(detalle.getIdDetallePedido())
                .producto(mapToProductoResponseDTO(detalle.getProducto()))
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }

    private ProductoResponseDTO mapToProductoResponseDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .estado(producto.getEstado())
                .categoria(mapToCategoriaResponseDTO(producto.getCategoria()))
                .build();
    }

    private CategoriaResponseDTO mapToCategoriaResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .estado(categoria.getEstado())
                .build();
    }

}