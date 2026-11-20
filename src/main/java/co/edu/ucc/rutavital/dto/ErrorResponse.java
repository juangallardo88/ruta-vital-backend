package co.edu.ucc.rutavital.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato JSON de los errores de la API (RNF-07). */
public record ErrorResponse(LocalDateTime fecha, int codigo, String mensaje, Map<String, String> detalles) {
}
