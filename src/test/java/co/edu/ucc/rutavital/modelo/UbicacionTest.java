package co.edu.ucc.rutavital.modelo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** RF-12: distancia aproximada en línea recta entre dos ubicaciones. */
class UbicacionTest {

    @Test
    void calculaLaDistanciaEnKilometros() {
        Ubicacion origen = new Ubicacion("Origen", 3.0, -76.0);
        Ubicacion destino = new Ubicacion("Destino", 3.1, -76.0);
        // 0,1 grados de latitud son unos 11,12 km
        assertThat(origen.distanciaA(destino)).isCloseTo(11.12, within(0.05));
    }

    @Test
    void laDistanciaAUnoMismoEsCero() {
        Ubicacion u = new Ubicacion("X", 3.45, -76.53);
        assertThat(u.distanciaA(u)).isEqualTo(0.0);
    }
}
