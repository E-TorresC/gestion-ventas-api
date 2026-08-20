package etctech.gestionventas.controller;

import etctech.gestionventas.dto.request.PedidoFiltroDTO;
import etctech.gestionventas.dto.request.PedidoRequestDTO;
import etctech.gestionventas.dto.response.ApiResponseDTO;
import etctech.gestionventas.dto.response.PedidoResponseDTO;
import etctech.gestionventas.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Registrar un nuevo pedido
     * POST /api/pedidos
     */
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> registrarPedido(
            @Valid @RequestBody PedidoRequestDTO request) {
        PedidoResponseDTO response = pedidoService.registrarPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtener un pedido por ID
     * GET /api/pedidos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedidoPorId(@PathVariable Long id) {
        PedidoResponseDTO response = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar pedidos activos con paginación
     * GET /api/pedidos
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<PedidoResponseDTO>>> listarPedidos(
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "fechaPedido", direction = Sort.Direction.DESC)
            }) Pageable pageable) {

        Page<PedidoResponseDTO> page = pedidoService.listarPedidos(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(page));
    }

    /**
     * Buscar pedidos con filtros dinámicos
     * POST /api/pedidos/buscar
     */
    @PostMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<Page<PedidoResponseDTO>>> buscarPedidosConFiltros(
            @RequestBody(required = false) PedidoFiltroDTO filtro,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "fechaPedido", direction = Sort.Direction.DESC)
            }) Pageable pageable) {

        if (filtro == null) {
            filtro = new PedidoFiltroDTO();
        }

        Page<PedidoResponseDTO> page = pedidoService.buscarPedidosConFiltros(filtro, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(page));
    }

    /**
     * Listar pedidos de un cliente
     * GET /api/pedidos/cliente/{idCliente}
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<ApiResponseDTO<Page<PedidoResponseDTO>>> listarPedidosPorCliente(
            @PathVariable Long idCliente,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "fechaPedido", direction = Sort.Direction.DESC)
            }) Pageable pageable) {

        Page<PedidoResponseDTO> page = pedidoService.listarPedidosPorCliente(idCliente, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(page));
    }

    /**
     * Cambiar estado de un pedido
     * PATCH /api/pedidos/{id}/estado?estado=CONFIRMADO
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestParam String estado) {
        PedidoResponseDTO response = pedidoService.cambiarEstadoPedido(id, estado);
        return ResponseEntity.ok(response);
    }

    /**
     * Anular un pedido
     * DELETE /api/pedidos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> anularPedido(@PathVariable Long id) {
        pedidoService.anularPedido(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener pedidos por rango de fechas
     * GET /api/pedidos/rango-fechas?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-12-31T23:59:59
     */
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPedidosPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<PedidoResponseDTO> pedidos = pedidoService.obtenerPedidosPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtener pedidos por rango de fechas con paginación
     * GET /api/pedidos/rango-fechas/paginado
     */
    @GetMapping("/rango-fechas/paginado")
    public ResponseEntity<Page<PedidoResponseDTO>> obtenerPedidosPorRangoFechasPaginado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @PageableDefault(size = 10, sort = "fechaPedido", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PedidoResponseDTO> page = pedidoService.obtenerPedidosPorRangoFechas(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(page);
    }

}