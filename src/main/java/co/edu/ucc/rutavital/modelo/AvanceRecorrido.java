package co.edu.ucc.rutavital.modelo;

import java.time.LocalDateTime;

/** Avance del recorrido (llegada al lugar o al centro), embebido en la emergencia. */
public class AvanceRecorrido {

    private TipoAvance tipo;
    private LocalDateTime fechaHora;

    public AvanceRecorrido() {
    }

    public AvanceRecorrido(TipoAvance tipo, LocalDateTime fechaHora) {
        this.tipo = tipo;
        this.fechaHora = fechaHora;
    }

    public TipoAvance getTipo() { return tipo; }
    public void setTipo(TipoAvance tipo) { this.tipo = tipo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
