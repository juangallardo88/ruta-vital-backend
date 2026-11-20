package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.Ubicacion;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Ubicación simulada recibida o enviada por la API. */
public record UbicacionDTO(
        @NotBlank(message = "El nombre del lugar es obligatorio") String nombre,
        @NotNull(message = "La latitud es obligatoria") @DecimalMin("-90") @DecimalMax("90") Double latitud,
        @NotNull(message = "La longitud es obligatoria") @DecimalMin("-180") @DecimalMax("180") Double longitud) {

    public Ubicacion aModelo() {
        return new Ubicacion(nombre.trim(), latitud, longitud);
    }

    public static UbicacionDTO desde(Ubicacion u) {
        return u == null ? null : new UbicacionDTO(u.getNombre(), u.getLatitud(), u.getLongitud());
    }
}
