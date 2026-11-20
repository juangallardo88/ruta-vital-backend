package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.modelo.NivelTrafico;
import co.edu.ucc.rutavital.modelo.Ruta;
import co.edu.ucc.rutavital.modelo.Ubicacion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** RF-09 con el ejemplo de la HU-09: la ruta corta con tráfico alto no siempre es la mejor. */
class RutaMenorTiempoTest {

    private final EstrategiaRuta estrategia = new RutaMenorTiempo();
    private final Ubicacion a = new Ubicacion("A", 3.4, -76.5);
    private final Ubicacion b = new Ubicacion("B", 3.5, -76.5);

    @Test
    void recomiendaLaDeMenorTiempoEstimado() {
        Ruta rutaA = new Ruta("Ruta A", a, b, 4, 10, NivelTrafico.ALTO);   // 17 min
        Ruta rutaB = new Ruta("Ruta B", a, b, 6, 12, NivelTrafico.BAJO);   // 12 min
        assertThat(estrategia.elegir(List.of(rutaA, rutaB))).contains(rutaB);
    }

    @Test
    void sinRutasNoHayRecomendacion() {
        assertThat(estrategia.elegir(List.of())).isEmpty();
    }
}
