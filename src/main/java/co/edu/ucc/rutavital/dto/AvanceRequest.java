package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.TipoAvance;
import jakarta.validation.constraints.NotNull;

public record AvanceRequest(@NotNull(message = "El tipo de avance es obligatorio") TipoAvance tipo) {
}
