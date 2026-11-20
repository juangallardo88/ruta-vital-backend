package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import jakarta.validation.constraints.NotNull;

public record EstadoAmbulanciaRequest(@NotNull(message = "El estado es obligatorio") EstadoAmbulancia estado) {
}
