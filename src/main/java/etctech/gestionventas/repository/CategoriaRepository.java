package etctech.gestionventas.repository;

import etctech.gestionventas.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Listar solo categorías activas con paginación
    Page<Categoria> findByEstadoTrue(Pageable pageable);

    // Listar solo categorías activas sin paginación
    List<Categoria> findByEstadoTrue();

    // Buscar categoría activa por ID
    Optional<Categoria> findByIdCategoriaAndEstadoTrue(Long id);

    // Verificar si existe una categoría activa con ese nombre
    boolean existsByNombreAndEstadoTrue(String nombre);

    // Verificar si existe una categoría activa con ese nombre (excluyendo un ID)
    @Query("SELECT COUNT(c) > 0 FROM Categoria c WHERE c.nombre = :nombre AND c.estado = true AND c.idCategoria != :id")
    boolean existsByNombreAndEstadoTrueAndIdCategoriaNot(@Param("nombre") String nombre, @Param("id") Long id);

    // Buscar categorías por nombre (contiene) y estado activo
    Page<Categoria> findByNombreContainingIgnoreCaseAndEstadoTrue(String nombre, Pageable pageable);

    // Contar categorías activas
    long countByEstadoTrue();

}