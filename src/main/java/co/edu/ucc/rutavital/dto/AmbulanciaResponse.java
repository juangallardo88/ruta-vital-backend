package co.edu.ucc.rutavital.dto;

import co.edu.ucc.rutavital.modelo.Ambulancia;
import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;

public record AmbulanciaResponse(String id, String placa, String codigoInterno, TipoAmbulancia tipo,
                                 UbicacionDTO ubicacion, EstadoAmbulancia estado, boolean activa) {

    public static AmbulanciaResponse desde(Ambulancia a) {
        return new AmbulanciaResponse(a.getId(), a.getPlaca(), a.getCodigoInterno(), a.getTipo(),
                UbicacionDTO.desde(a.getUbicacion()), a.getEstado(), a.isActiva());
    }
}
