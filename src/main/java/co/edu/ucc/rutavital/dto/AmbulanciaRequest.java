package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Datos para registrar o actualizar una ambulancia (RF-01, RF-03). */
public record AmbulanciaRequest(
        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "^[A-Za-z]{3}-?[0-9]{3}$", message = "La placa debe tener el formato ABC123")
        String placa,
        @NotBlank(message = "El código interno es obligatorio") String codigoInterno,
        @NotNull(message = "El tipo es obligatorio") TipoAmbulancia tipo,
        @NotNull(message = "La ubicación es obligatoria") @Valid UbicacionDTO ubicacion,
        @NotNull(message = "El estado inicial es obligatorio") EstadoAmbulancia estado) {

    /** Placa normalizada: mayúsculas y sin guion. */
    public String placaNormalizada() {
        return placa.replace("-", "").toUpperCase();
    }
}
