package co.edu.ucc.rutavital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** Datos para finalizar una emergencia (RF-16). El tiempo simulado se usa si no hay llegada al centro. */
public record FinalizacionRequest(
        @NotBlank(message = "El resultado de la atención es obligatorio") String resultado,
        @Positive(message = "El tiempo del recorrido debe ser mayor que cero") Double tiempoRecorridoMin) {
}
