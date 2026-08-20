package etctech.gestionventas.specification;

import etctech.gestionventas.dto.request.ProductoFiltroDTO;
import etctech.gestionventas.entity.Producto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductoSpecification {

    /**
     * Crear Specification dinámica basada en los filtros
     */
    public static Specification<Producto> filtrarProductos(ProductoFiltroDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Siempre mostrar solo productos activos (a menos que se pida explícitamente)
            if (filtro.getEstado() == null || filtro.getEstado()) {
                predicates.add(criteriaBuilder.isTrue(root.get("estado")));
            }

            // Filtro por nombre (contiene, ignorando mayúsculas/minúsculas)
            if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        "%" + filtro.getNombre().toLowerCase() + "%"
                ));
            }

            // Filtro por precio mínimo
            if (filtro.getPrecioMinimo() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("precio"),
                        filtro.getPrecioMinimo()
                ));
            }

            // Filtro por precio máximo
            if (filtro.getPrecioMaximo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("precio"),
                        filtro.getPrecioMaximo()
                ));
            }

            // Filtro por stock mínimo
            if (filtro.getStockMinimo() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("stock"),
                        filtro.getStockMinimo()
                ));
            }

            // Filtro por stock máximo
            if (filtro.getStockMaximo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("stock"),
                        filtro.getStockMaximo()
                ));
            }

            // Filtro por categoría
            if (filtro.getIdCategoria() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("categoria").get("idCategoria"),
                        filtro.getIdCategoria()
                ));
            }

            // Filtro por nombre de categoría (contiene)
            if (filtro.getNombreCategoria() != null && !filtro.getNombreCategoria().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("categoria").get("nombre")),
                        "%" + filtro.getNombreCategoria().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Specification para productos con stock bajo
     */
    public static Specification<Producto> stockMenorA(Integer limite) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Solo productos activos
            predicates.add(criteriaBuilder.isTrue(root.get("estado")));

            // Stock menor al límite
            predicates.add(criteriaBuilder.lessThan(root.get("stock"), limite));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Specification para productos en stock
     */
    public static Specification<Producto> conStock() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("stock"), 0);
    }

    /**
     * Specification para productos sin stock
     */
    public static Specification<Producto> sinStock() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("stock"), 0);
    }

    /**
     * Specification para productos por rango de precios
     */
    public static Specification<Producto> precioEntre(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (min != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), min));
            }
            if (max != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), max));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Método para combinar Specifications
     */
    public static Specification<Producto> combinarFiltros(ProductoFiltroDTO filtro) {
        Specification<Producto> spec = (root, query, criteriaBuilder) -> null;

        if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("nombre")),
                            "%" + filtro.getNombre().toLowerCase() + "%"
                    )
            );
        }

        if (filtro.getPrecioMinimo() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), filtro.getPrecioMinimo())
            );
        }

        if (filtro.getPrecioMaximo() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("precio"), filtro.getPrecioMaximo())
            );
        }

        if (filtro.getIdCategoria() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("categoria").get("idCategoria"), filtro.getIdCategoria())
            );
        }

        // Estado activo por defecto
        if (filtro.getEstado() == null || filtro.getEstado()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.isTrue(root.get("estado"))
            );
        }

        return spec;
    }

}