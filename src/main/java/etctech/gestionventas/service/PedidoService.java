package etctech.gestionventas.service;

import etctech.gestionventas.dto.request.PedidoFiltroDTO;
import etctech.gestionventas.dto.request.PedidoRequestDTO;
import etctech.gestionventas.dto.response.PedidoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoService {

    PedidoResponseDTO registrarPedido(PedidoRequestDTO request);

    PedidoResponseDTO obtenerPedidoPorId(Long id);

    Page<PedidoResponseDTO> listarPedidos(Pageable pageable);

    Page<PedidoResponseDTO> listarPedidosPorCliente(Long idCliente, Pageable pageable);

    Page<PedidoResponseDTO> buscarPedidosConFiltros(PedidoFiltroDTO filtro, Pageable pageable);

    PedidoResponseDTO cambiarEstadoPedido(Long id, String nuevoEstado);

    void anularPedido(Long id);

    List<PedidoResponseDTO> obtenerPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<PedidoResponseDTO> obtenerPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

}