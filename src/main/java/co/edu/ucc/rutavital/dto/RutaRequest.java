package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.NivelTrafico;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Datos para registrar o actualizar una ruta (RF-07). */
public record RutaRequest(
        @NotBlank(message = "El nombre de la ruta es obligatorio") String nombre,
        @NotNull(message = "El origen es obligatorio") @Valid UbicacionDTO origen,
        @NotNull(message = "El destino es obligatorio") @Valid UbicacionDTO destino,
        @NotNull(message = "La distancia es obligatoria") @Positive(message = "La distancia debe ser mayor que cero") Double distanciaKm,
        @NotNull(message = "El tiempo base es obligatorio") @Positive(message = "El tiempo base debe ser mayor que cero") Double tiempoBaseMin,
        @NotNull(message = "El nivel de tráfico es obligatorio") NivelTrafico nivelTrafico) {
}
