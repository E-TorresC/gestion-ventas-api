package etctech.gestionventas.service.impl;

import etctech.gestionventas.dto.request.ClienteRequestDTO;
import etctech.gestionventas.dto.response.ClienteResponseDTO;
import etctech.gestionventas.entity.Cliente;
import etctech.gestionventas.exception.BusinessException;
import etctech.gestionventas.exception.ResourceNotFoundException;
import etctech.gestionventas.repository.ClienteRepository;
import etctech.gestionventas.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    public ClienteResponseDTO crearCliente(ClienteRequestDTO request) {
        log.info("Creando nuevo cliente: {} {}", request.getNombres(), request.getApellidos());

        // Validar que no exista un cliente con el mismo email
        if (clienteRepository.existsByEmailAndEstadoTrue(request.getEmail())) {
            throw new BusinessException("Ya existe un cliente activo con el email: " + request.getEmail());
        }

        // Crear la entidad
        Cliente cliente = new Cliente();
        cliente.setNombres(request.getNombres().trim());
        cliente.setApellidos(request.getApellidos().trim());
        cliente.setEmail(request.getEmail().trim().toLowerCase());
        cliente.setTelefono(request.getTelefono());
        cliente.setEstado(true);

        // Guardar
        Cliente saved = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", saved.getIdCliente());

        return mapToResponseDTO(saved);
    }

    @Override
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO request) {
        log.info("Actualizando cliente con ID: {}", id);

        // Buscar el cliente activo
        Cliente cliente = clienteRepository.findByIdClienteAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Validar que el email no exista en otro cliente activo
        if (clienteRepository.existsByEmailAndEstadoTrueAndIdClienteNot(request.getEmail().trim().toLowerCase(), id)) {
            throw new BusinessException("Ya existe otro cliente activo con el email: " + request.getEmail());
        }

        // Actualizar datos
        cliente.setNombres(request.getNombres().trim());
        cliente.setApellidos(request.getApellidos().trim());
        cliente.setEmail(request.getEmail().trim().toLowerCase());
        cliente.setTelefono(request.getTelefono());

        Cliente updated = clienteRepository.save(cliente);
        log.info("Cliente actualizado exitosamente con ID: {}", updated.getIdCliente());

        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorId(Long id) {
        log.info("Obteniendo cliente con ID: {}", id);

        Cliente cliente = clienteRepository.findByIdClienteAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        return mapToResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientesActivos(Pageable pageable) {
        log.info("Listando clientes activos con paginación");

        return clienteRepository.findByEstadoTrue(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> buscarClientes(String searchTerm, Pageable pageable) {
        log.info("Buscando clientes por: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return listarClientesActivos(pageable);
        }

        return clienteRepository.searchByNombreOrApellido(searchTerm.trim(), pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public void eliminarCliente(Long id) {
        log.info("Eliminando lógicamente cliente con ID: {}", id);

        Cliente cliente = clienteRepository.findByIdClienteAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Verificar si el cliente tiene pedidos activos
        boolean tienePedidosActivos = cliente.getPedidos().stream()
                .anyMatch(pedido -> pedido.getEstado() != null &&
                        !pedido.getEstado().equals("ANULADO") &&
                        !pedido.getEstado().equals("ENTREGADO"));

        if (tienePedidosActivos) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene pedidos activos no finalizados");
        }

        // Eliminación lógica
        cliente.setEstado(false);
        clienteRepository.save(cliente);
        log.info("Cliente eliminado lógicamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeClienteActivo(Long id) {
        return clienteRepository.findByIdClienteAndEstadoTrue(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientesTopCompras(Pageable pageable) {
        log.info("Listando clientes con mayor monto de compras");

        return clienteRepository.findClientesByMontoTotalCompras(pageable)
                .map(this::mapToResponseDTO);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private ClienteResponseDTO mapToResponseDTO(Cliente cliente) {
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

}