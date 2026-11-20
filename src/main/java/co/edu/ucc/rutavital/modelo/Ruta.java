package co.edu.ucc.rutavital.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Ruta candidata entre un origen y un destino, con datos simulados. */
@Document(collection = "rutas")
public class Ruta {

    @Id
    private String id;
    private String nombre;
    private Ubicacion origen;
    private Ubicacion destino;
    private double distanciaKm;
    private double tiempoBaseMin;
    private NivelTrafico nivelTrafico;
    private boolean activa = true;

    public Ruta() {
    }

    public Ruta(String nombre, Ubicacion origen, Ubicacion destino, double distanciaKm, double tiempoBaseMin, NivelTrafico nivelTrafico) {
        this.nombre = nombre;
        this.origen = origen;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
        this.tiempoBaseMin = tiempoBaseMin;
        this.nivelTrafico = nivelTrafico;
        this.activa = true;
    }

    /** Tiempo base por el factor de tráfico (RF-08). Patrón Experto. */
    public double calcularTiempoEstimado() {
        return Math.round(tiempoBaseMin * nivelTrafico.getFactor() * 10.0) / 10.0;
    }

    /** Baja lógica de la ruta (RF-07). */
    public void desactivar() {
        activa = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Ubicacion getOrigen() { return origen; }
    public void setOrigen(Ubicacion origen) { this.origen = origen; }
    public Ubicacion getDestino() { return destino; }
    public void setDestino(Ubicacion destino) { this.destino = destino; }
    public double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(double distanciaKm) { this.distanciaKm = distanciaKm; }
    public double getTiempoBaseMin() { return tiempoBaseMin; }
    public void setTiempoBaseMin(double tiempoBaseMin) { this.tiempoBaseMin = tiempoBaseMin; }
    public NivelTrafico getNivelTrafico() { return nivelTrafico; }
    public void setNivelTrafico(NivelTrafico nivelTrafico) { this.nivelTrafico = nivelTrafico; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
