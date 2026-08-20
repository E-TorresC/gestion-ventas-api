package etctech.gestionventas.specification;

import etctech.gestionventas.dto.request.PedidoFiltroDTO;
import etctech.gestionventas.entity.Pedido;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoSpecification {

    /**
     * Crear Specification dinámica basada en los filtros
     */
    public static Specification<Pedido> filtrarPedidos(PedidoFiltroDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por cliente
            if (filtro.getIdCliente() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("cliente").get("idCliente"),
                        filtro.getIdCliente()
                ));
            }

            // Filtro por estado
            if (filtro.getEstado() != null && !filtro.getEstado().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("estado"),
                        filtro.getEstado()
                ));
            }

            // Filtro por fecha inicio
            if (filtro.getFechaInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("fechaPedido"),
                        filtro.getFechaInicio()
                ));
            }

            // Filtro por fecha fin
            if (filtro.getFechaFin() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("fechaPedido"),
                        filtro.getFechaFin()
                ));
            }

            // Filtro por total mínimo
            if (filtro.getTotalMinimo() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("total"),
                        filtro.getTotalMinimo()
                ));
            }

            // Filtro por total máximo
            if (filtro.getTotalMaximo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("total"),
                        filtro.getTotalMaximo()
                ));
            }

            // Excluir pedidos anulados por defecto (a menos que se pidan explícitamente)
            if (filtro.getEstado() == null || !filtro.getEstado().equals("ANULADO")) {
                predicates.add(criteriaBuilder.notEqual(
                        root.get("estado"),
                        "ANULADO"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}