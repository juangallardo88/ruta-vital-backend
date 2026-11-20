package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.AvanceRecorrido;
import co.edu.ucc.rutavital.modelo.TipoAvance;

import java.time.LocalDateTime;

public record AvanceDTO(TipoAvance tipo, LocalDateTime fechaHora) {
    public static AvanceDTO desde(AvanceRecorrido a) {
        return new AvanceDTO(a.getTipo(), a.getFechaHora());
    }
}
