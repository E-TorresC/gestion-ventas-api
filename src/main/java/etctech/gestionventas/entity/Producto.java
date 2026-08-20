package etctech.gestionventas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detallesPedido = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.estado = true;
        if (this.stock == null) {
            this.stock = 0;
        }
    }

    // Métodos de negocio
    public boolean tieneStockSuficiente(Integer cantidad) {
        return this.stock >= cantidad;
    }

    public void descontarStock(Integer cantidad) {
        if (!tieneStockSuficiente(cantidad)) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + this.nombre);
        }
        this.stock -= cantidad;
    }

    public void aumentarStock(Integer cantidad) {
        this.stock += cantidad;
    }

    public boolean isActivo() {
        return this.estado != null && this.estado;
    }
}