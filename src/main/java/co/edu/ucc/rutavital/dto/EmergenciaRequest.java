package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.Prioridad;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** Datos para registrar una emergencia (RF-11). */
public record EmergenciaRequest(
        @NotBlank(message = "La descripción es obligatoria") String descripcion,
        @NotNull(message = "La prioridad es obligatoria") Prioridad prioridad,
        @NotNull(message = "El tipo de ambulancia requerido es obligatorio") TipoAmbulancia tipoRequerido,
        @NotNull(message = "El origen es obligatorio") @Valid UbicacionDTO origen,
        @NotNull(message = "El destino es obligatorio") @Valid UbicacionDTO destino,
        @NotNull(message = "La fecha y hora son obligatorias") LocalDateTime fechaHora) {
}
