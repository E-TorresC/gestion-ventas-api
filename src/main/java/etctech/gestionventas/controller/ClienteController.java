package etctech.gestionventas.controller;

import etctech.gestionventas.dto.request.ClienteFiltroDTO;
import etctech.gestionventas.dto.request.ClienteRequestDTO;
import etctech.gestionventas.dto.response.ClienteResponseDTO;
import etctech.gestionventas.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Crear un nuevo cliente
     * POST /api/clientes
     */
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(
            @Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO response = clienteService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar un cliente existente
     * PUT /api/clientes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO response = clienteService.actualizarCliente(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener un cliente por ID
     * GET /api/clientes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorId(@PathVariable Long id) {
        ClienteResponseDTO response = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos los clientes activos con paginación
     * GET /api/clientes
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(
            @PageableDefault(size = 10, sort = "apellidos", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClienteResponseDTO> page = clienteService.listarClientesActivos(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Buscar clientes por nombre o apellido
     * GET /api/clientes/buscar?search=...
     */
    @GetMapping("/buscar")
    public ResponseEntity<Page<ClienteResponseDTO>> buscarClientes(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "apellidos", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClienteResponseDTO> page = clienteService.buscarClientes(search, pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/buscar-avanzada")
    public ResponseEntity<Page<ClienteResponseDTO>> buscarClientesAvanzado(
            @RequestBody(required = false) ClienteFiltroDTO filtro,
            @PageableDefault(
                    size = 10,
                    sort = "apellidos",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        if (filtro == null) {
            filtro = new ClienteFiltroDTO();
        }

        Page<ClienteResponseDTO> page =
                clienteService.buscarClientesAvanzado(filtro, pageable);

        return ResponseEntity.ok(page);
    }

    /**
     * Listar clientes con mayor monto de compras
     * GET /api/clientes/top-compras
     */
    @GetMapping("/top-compras")
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientesTopCompras(
            @PageableDefault(size = 10, sort = "totalCompras", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ClienteResponseDTO> page = clienteService.listarClientesTopCompras(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Eliminar lógicamente un cliente
     * DELETE /api/clientes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verificar si existe un cliente activo
     * GET /api/clientes/{id}/existe
     */
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeCliente(@PathVariable Long id) {
        boolean existe = clienteService.existeClienteActivo(id);
        return ResponseEntity.ok(existe);
    }

}