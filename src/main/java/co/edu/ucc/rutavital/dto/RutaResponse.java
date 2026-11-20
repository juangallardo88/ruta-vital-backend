package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.NivelTrafico;
import co.edu.ucc.rutavital.modelo.Ruta;

/** Ruta con su tiempo estimado (RF-08). En las alternativas se indica cuál es la recomendada (RF-10). */
public record RutaResponse(String id, String nombre, UbicacionDTO origen, UbicacionDTO destino,
                           double distanciaKm, double tiempoBaseMin, NivelTrafico nivelTrafico,
                           double tiempoEstimadoMin, boolean activa, boolean recomendada) {

    public static RutaResponse desde(Ruta r) {
        return desde(r, false);
    }

    public static RutaResponse desde(Ruta r, boolean recomendada) {
        return new RutaResponse(r.getId(), r.getNombre(), UbicacionDTO.desde(r.getOrigen()), UbicacionDTO.desde(r.getDestino()),
                r.getDistanciaKm(), r.getTiempoBaseMin(), r.getNivelTrafico(), r.calcularTiempoEstimado(), r.isActiva(), recomendada);
    }
}
