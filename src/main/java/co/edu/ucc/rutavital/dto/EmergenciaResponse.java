package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.Emergencia;
import co.edu.ucc.rutavital.modelo.EstadoEmergencia;
import co.edu.ucc.rutavital.modelo.Prioridad;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;

import java.time.LocalDateTime;
import java.util.List;

/** Emergencia con los datos de la ambulancia y la ruta asignadas (RF-17). */
public record EmergenciaResponse(String id, String descripcion, Prioridad prioridad, TipoAmbulancia tipoRequerido,
                                 UbicacionDTO origen, UbicacionDTO destino, LocalDateTime fechaHora,
                                 EstadoEmergencia estado, Double tiempoRecorridoMin, String resultado,
                                 String ambulanciaId, String ambulanciaPlaca, String rutaId, String rutaNombre,
                                 List<AvanceDTO> avances) {

    public static EmergenciaResponse desde(Emergencia e, String ambulanciaPlaca, String rutaNombre) {
        return new EmergenciaResponse(e.getId(), e.getDescripcion(), e.getPrioridad(), e.getTipoRequerido(),
                UbicacionDTO.desde(e.getOrigen()), UbicacionDTO.desde(e.getDestino()), e.getFechaHora(),
                e.getEstado(), e.getTiempoRecorridoMin(), e.getResultado(),
                e.getAmbulanciaId(), ambulanciaPlaca, e.getRutaId(), rutaNombre,
                e.getAvances().stream().map(AvanceDTO::desde).toList());
    }
}
