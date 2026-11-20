package co.edu.ucc.rutavital.modelo;

import co.edu.ucc.rutavital.excepcion.ConflictoException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Emergencia registrada por un operador. Valida sus propias transiciones de estado
 * (patrón Experto) y crea sus avances del recorrido (patrón Creador).
 */
@Document(collection = "emergencias")
public class Emergencia {

    /** Transiciones permitidas (RF-15). */
    private static final Map<EstadoEmergencia, Set<EstadoEmergencia>> TRANSICIONES = new EnumMap<>(EstadoEmergencia.class);

    static {
        TRANSICIONES.put(EstadoEmergencia.REGISTRADA, EnumSet.of(EstadoEmergencia.EN_CURSO, EstadoEmergencia.CANCELADA));
        TRANSICIONES.put(EstadoEmergencia.EN_CURSO, EnumSet.of(EstadoEmergencia.FINALIZADA, EstadoEmergencia.CANCELADA));
        TRANSICIONES.put(EstadoEmergencia.FINALIZADA, EnumSet.noneOf(EstadoEmergencia.class));
        TRANSICIONES.put(EstadoEmergencia.CANCELADA, EnumSet.noneOf(EstadoEmergencia.class));
    }

    @Id
    private String id;
    private String descripcion;
    private Prioridad prioridad;
    private TipoAmbulancia tipoRequerido;
    private Ubicacion origen;
    private Ubicacion destino;
    private LocalDateTime fechaHora;
    private EstadoEmergencia estado;
    private Double tiempoRecorridoMin;
    private String resultado;

    /** Referencias por id (asociaciones del diagrama). */
    private String operadorId;
    private String ambulanciaId;
    private String rutaId;

    /** Composición: los avances se guardan embebidos. */
    private List<AvanceRecorrido> avances = new ArrayList<>();

    public Emergencia() {
    }

    public Emergencia(String descripcion, Prioridad prioridad, TipoAmbulancia tipoRequerido,
                      Ubicacion origen, Ubicacion destino, LocalDateTime fechaHora, String operadorId) {
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.tipoRequerido = tipoRequerido;
        this.origen = origen;
        this.destino = destino;
        this.fechaHora = fechaHora;
        this.operadorId = operadorId;
        this.estado = EstadoEmergencia.REGISTRADA;
    }

    /** Confirma la asignación (RF-13). La ruta puede ser nula si no hay rutas registradas. */
    public void asignar(Ambulancia ambulancia, Ruta ruta) {
        validarTransicion(EstadoEmergencia.EN_CURSO);
        this.ambulanciaId = ambulancia.getId();
        this.rutaId = ruta != null ? ruta.getId() : null;
        this.estado = EstadoEmergencia.EN_CURSO;
    }

    /** Registra un avance del recorrido (RF-14). Patrón Creador. */
    public AvanceRecorrido registrarAvance(TipoAvance tipo) {
        if (estado != EstadoEmergencia.EN_CURSO) {
            throw new ConflictoException("Solo se pueden registrar avances en emergencias en curso");
        }
        if (buscarAvance(tipo).isPresent()) {
            throw new ConflictoException("Ese avance ya fue registrado");
        }
        if (tipo == TipoAvance.LLEGADA_CENTRO && buscarAvance(TipoAvance.LLEGADA_LUGAR).isEmpty()) {
            throw new ConflictoException("Primero debe registrarse la llegada al lugar de la emergencia");
        }
        AvanceRecorrido avance = new AvanceRecorrido(tipo, LocalDateTime.now());
        avances.add(avance);
        return avance;
    }

    /**
     * Finaliza la emergencia (RF-16). Si se registró la llegada al centro, el tiempo del
     * recorrido se calcula desde el registro de la emergencia; si no, se usa el dato simulado.
     */
    public void finalizar(String resultado, Double tiempoSimuladoMin) {
        validarTransicion(EstadoEmergencia.FINALIZADA);
        Optional<AvanceRecorrido> llegadaCentro = buscarAvance(TipoAvance.LLEGADA_CENTRO);
        if (llegadaCentro.isPresent()) {
            long segundos = Duration.between(fechaHora, llegadaCentro.get().getFechaHora()).getSeconds();
            this.tiempoRecorridoMin = Math.round(Math.max(segundos, 0) / 6.0) / 10.0;
        } else if (tiempoSimuladoMin != null) {
            this.tiempoRecorridoMin = tiempoSimuladoMin;
        } else {
            throw new IllegalArgumentException("Ingrese el tiempo del recorrido o registre la llegada al centro de atención");
        }
        this.resultado = resultado;
        this.estado = EstadoEmergencia.FINALIZADA;
    }

    /** Cancela la emergencia (RF-15). Devuelve true si estaba en curso y hay que liberar la ambulancia. */
    public boolean cancelar() {
        boolean estabaEnCurso = estado == EstadoEmergencia.EN_CURSO;
        validarTransicion(EstadoEmergencia.CANCELADA);
        this.estado = EstadoEmergencia.CANCELADA;
        return estabaEnCurso;
    }

    private void validarTransicion(EstadoEmergencia nuevo) {
        if (!TRANSICIONES.get(estado).contains(nuevo)) {
            throw new ConflictoException("Transición de estado no permitida: " + estado + " -> " + nuevo);
        }
    }

    private Optional<AvanceRecorrido> buscarAvance(TipoAvance tipo) {
        return avances.stream().filter(a -> a.getTipo() == tipo).findFirst();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }
    public TipoAmbulancia getTipoRequerido() { return tipoRequerido; }
    public void setTipoRequerido(TipoAmbulancia tipoRequerido) { this.tipoRequerido = tipoRequerido; }
    public Ubicacion getOrigen() { return origen; }
    public void setOrigen(Ubicacion origen) { this.origen = origen; }
    public Ubicacion getDestino() { return destino; }
    public void setDestino(Ubicacion destino) { this.destino = destino; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public EstadoEmergencia getEstado() { return estado; }
    public void setEstado(EstadoEmergencia estado) { this.estado = estado; }
    public Double getTiempoRecorridoMin() { return tiempoRecorridoMin; }
    public void setTiempoRecorridoMin(Double tiempoRecorridoMin) { this.tiempoRecorridoMin = tiempoRecorridoMin; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getOperadorId() { return operadorId; }
    public void setOperadorId(String operadorId) { this.operadorId = operadorId; }
    public String getAmbulanciaId() { return ambulanciaId; }
    public void setAmbulanciaId(String ambulanciaId) { this.ambulanciaId = ambulanciaId; }
    public String getRutaId() { return rutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }
    public List<AvanceRecorrido> getAvances() { return avances; }
    public void setAvances(List<AvanceRecorrido> avances) { this.avances = avances; }
}
