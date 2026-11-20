package co.edu.ucc.rutavital.modelo;

import co.edu.ucc.rutavital.excepcion.ConflictoException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Ambulancia registrada. Controla sus propias reglas de disponibilidad (patrón Experto).
 */
@Document(collection = "ambulancias")
public class Ambulancia {

    @Id
    private String id;

    @Indexed(unique = true)
    private String placa;

    @Indexed(unique = true)
    private String codigoInterno;

    private TipoAmbulancia tipo;
    private Ubicacion ubicacion;
    private EstadoAmbulancia estado;
    private boolean activa = true;

    public Ambulancia() {
    }

    public Ambulancia(String placa, String codigoInterno, TipoAmbulancia tipo, Ubicacion ubicacion, EstadoAmbulancia estado) {
        this.placa = placa;
        this.codigoInterno = codigoInterno;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.activa = true;
    }

    /** Solo las ambulancias activas y disponibles pueden asignarse (RF-06). */
    public boolean estaDisponible() {
        return activa && estado == EstadoAmbulancia.DISPONIBLE;
    }

    /** Pasa a En servicio al confirmar una asignación (RF-13). */
    public void ponerEnServicio() {
        if (!estaDisponible()) {
            throw new ConflictoException("La ambulancia " + placa + " ya no está disponible");
        }
        estado = EstadoAmbulancia.EN_SERVICIO;
    }

    /** Vuelve a Disponible al finalizar o cancelar la emergencia (RF-15, RF-16). */
    public void liberar() {
        if (estado == EstadoAmbulancia.EN_SERVICIO) {
            estado = EstadoAmbulancia.DISPONIBLE;
        }
    }

    /** Cambio manual de disponibilidad del administrador (RF-05). */
    public void cambiarDisponibilidad(EstadoAmbulancia nuevoEstado) {
        if (nuevoEstado == EstadoAmbulancia.EN_SERVICIO) {
            throw new IllegalArgumentException("El estado En servicio solo lo asigna el sistema");
        }
        if (estado == EstadoAmbulancia.EN_SERVICIO) {
            throw new ConflictoException("No se puede cambiar el estado de una ambulancia en servicio");
        }
        estado = nuevoEstado;
    }

    /** Baja lógica (RF-04). */
    public void desactivar() {
        if (estado == EstadoAmbulancia.EN_SERVICIO) {
            throw new ConflictoException("No se puede desactivar una ambulancia en servicio");
        }
        activa = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }
    public TipoAmbulancia getTipo() { return tipo; }
    public void setTipo(TipoAmbulancia tipo) { this.tipo = tipo; }
    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }
    public EstadoAmbulancia getEstado() { return estado; }
    public void setEstado(EstadoAmbulancia estado) { this.estado = estado; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
