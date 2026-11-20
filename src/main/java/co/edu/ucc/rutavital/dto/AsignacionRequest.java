package co.edu.ucc.rutavital.dto;

import jakarta.validation.constraints.NotBlank;

/** Confirmación de la asignación. La ruta es opcional si no hay rutas para el trayecto (RF-13). */
public record AsignacionRequest(
        @NotBlank(message = "Debe elegir una ambulancia") String ambulanciaId,
        String rutaId) {
}
