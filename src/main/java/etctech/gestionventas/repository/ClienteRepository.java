package etctech.gestionventas.repository;

import etctech.gestionventas.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    // Listar solo clientes activos con paginación
    Page<Cliente> findByEstadoTrue(Pageable pageable);

    // Buscar cliente activo por ID
    Optional<Cliente> findByIdClienteAndEstadoTrue(Long id);

    // Verificar si existe un cliente activo con ese email
    boolean existsByEmailAndEstadoTrue(String email);

    // Verificar si existe un cliente activo con ese email (excluyendo un ID)
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.email = :email AND c.estado = true AND c.idCliente != :id")
    boolean existsByEmailAndEstadoTrueAndIdClienteNot(@Param("email") String email, @Param("id") Long id);

    // Buscar clientes por nombre o apellido (contiene)
    @Query("SELECT c FROM Cliente c WHERE c.estado = true AND " +
            "(LOWER(c.nombres) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Cliente> searchByNombreOrApellido(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Buscar clientes por email (contiene)
    Page<Cliente> findByEmailContainingIgnoreCaseAndEstadoTrue(String email, Pageable pageable);

    // Contar clientes activos
    long countByEstadoTrue();

    // Obtener clientes ordenados por monto total de compras (para reportes)
    @Query("SELECT c FROM Cliente c JOIN c.pedidos p WHERE p.estado != 'ANULADO' AND c.estado = true " +
            "GROUP BY c.idCliente, c.nombres, c.apellidos, c.email, c.telefono, c.estado, c.fechaRegistro " +
            "ORDER BY SUM(p.total) DESC")
    Page<Cliente> findClientesByMontoTotalCompras(Pageable pageable);

    // Buscar con Specifications y paginación
    Page<Cliente> findAll(Specification<Cliente> spec, Pageable pageable);

    // Contar con Specifications
    long count(Specification<Cliente> spec);

}