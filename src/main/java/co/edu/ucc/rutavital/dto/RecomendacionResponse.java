package co.edu.ucc.rutavital.dto;

import java.util.List;

/**
 * Recomendaciones para una emergencia: la ambulancia más cercana del tipo requerido (RF-12)
 * y las rutas evaluadas con la recomendada (RF-09, RF-10). Los mensajes explican cuando no hay opciones.
 */
public record RecomendacionResponse(AmbulanciaResponse ambulancia, Double distanciaKm, String mensajeAmbulancia,
                                    List<AmbulanciaResponse> otrasDisponibles,
                                    List<RutaResponse> rutas, String mensajeRuta) {
}
