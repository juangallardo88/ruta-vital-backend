package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.modelo.Ruta;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Recomienda la ruta con menor tiempo estimado (RF-09). */
@Component
public class RutaMenorTiempo implements EstrategiaRuta {

    @Override
    public Optional<Ruta> elegir(List<Ruta> rutas) {
        return rutas.stream().min(Comparator.comparingDouble(Ruta::calcularTiempoEstimado));
    }
}
