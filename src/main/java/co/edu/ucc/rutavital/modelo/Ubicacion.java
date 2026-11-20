package co.edu.ucc.rutavital.modelo;

/**
 * Ubicación simulada (nombre y coordenadas). Se guarda embebida dentro de
 * ambulancias, rutas y emergencias.
 */
public class Ubicacion {

    private static final double RADIO_TIERRA_KM = 6371.0;

    private String nombre;
    private double latitud;
    private double longitud;

    public Ubicacion() {
    }

    public Ubicacion(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * Distancia aproximada en línea recta (fórmula de Haversine), en kilómetros.
     * Patrón Experto: la ubicación tiene los datos para calcularla (RF-12).
     */
    public double distanciaA(Ubicacion otra) {
        double dLat = Math.toRadians(otra.latitud - latitud);
        double dLon = Math.toRadians(otra.longitud - longitud);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitud)) * Math.cos(Math.toRadians(otra.latitud))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * RADIO_TIERRA_KM * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}
