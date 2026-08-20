package etctech.gestionventas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDTO<T> {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return success(data, "Operación exitosa");
    }

    public static <T> ApiResponseDTO<T> created(T data) {
        return ApiResponseDTO.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(201)
                .message("Recurso creado exitosamente")
                .data(data)
                .build();
    }

    public static ApiResponseDTO<Void> deleted() {
        return ApiResponseDTO.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(204)
                .message("Recurso eliminado exitosamente")
                .build();
    }

    public static ApiResponseDTO<Void> noContent() {
        return ApiResponseDTO.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(204)
                .message("Operación exitosa sin contenido")
                .build();
    }

}