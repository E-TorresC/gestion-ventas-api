package etctech.gestionventas.specification;

import etctech.gestionventas.dto.request.ClienteFiltroDTO;
import etctech.gestionventas.entity.Cliente;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteSpecification {

    /**
     * Crear Specification dinámica basada en los filtros
     */
    public static Specification<Cliente> filtrarClientes(ClienteFiltroDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Estado
            if (filtro.getEstado() != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), filtro.getEstado()));
            }

            // Nombre (contiene)
            if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombres")),
                        "%" + filtro.getNombre().toLowerCase() + "%"
                ));
            }

            // Apellido (contiene)
            if (filtro.getApellido() != null && !filtro.getApellido().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("apellidos")),
                        "%" + filtro.getApellido().toLowerCase() + "%"
                ));
            }

            // Email (contiene)
            if (filtro.getEmail() != null && !filtro.getEmail().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + filtro.getEmail().toLowerCase() + "%"
                ));
            }

            // Teléfono (contiene)
            if (filtro.getTelefono() != null && !filtro.getTelefono().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("telefono"),
                        "%" + filtro.getTelefono() + "%"
                ));
            }

            // Fecha registro inicio
            if (filtro.getFechaRegistroInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("fechaRegistro"),
                        filtro.getFechaRegistroInicio()
                ));
            }

            // Fecha registro fin
            if (filtro.getFechaRegistroFin() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("fechaRegistro"),
                        filtro.getFechaRegistroFin()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Specification para clientes activos
     */
    public static Specification<Cliente> activos() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("estado"));
    }

    /**
     * Specification para clientes con compras (tienen pedidos)
     */
    public static Specification<Cliente> conCompras() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotEmpty(root.get("pedidos"));
    }

}