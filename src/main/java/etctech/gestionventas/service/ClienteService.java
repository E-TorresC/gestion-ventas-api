package etctech.gestionventas.service;

import etctech.gestionventas.dto.request.ClienteFiltroDTO;
import etctech.gestionventas.dto.request.ClienteRequestDTO;
import etctech.gestionventas.dto.response.ClienteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO request);

    ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO request);

    ClienteResponseDTO obtenerClientePorId(Long id);

    Page<ClienteResponseDTO> listarClientesActivos(Pageable pageable);

    Page<ClienteResponseDTO> buscarClientes(String searchTerm, Pageable pageable);

    void eliminarCliente(Long id);

    boolean existeClienteActivo(Long id);

    Page<ClienteResponseDTO> listarClientesTopCompras(Pageable pageable);

    Page<ClienteResponseDTO> buscarClientesAvanzado(
            ClienteFiltroDTO filtro,
            Pageable pageable
    );

}