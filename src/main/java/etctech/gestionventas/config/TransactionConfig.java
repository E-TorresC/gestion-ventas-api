package etctech.gestionventas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    // Configuración de transacciones
    // Spring Boot maneja automáticamente la configuración
    // con @EnableTransactionManagement
}