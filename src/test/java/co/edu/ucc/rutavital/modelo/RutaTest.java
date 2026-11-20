package co.edu.ucc.rutavital.modelo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** RF-08: tiempo estimado = tiempo base x factor de tráfico. */
class RutaTest {

    private final Ubicacion a = new Ubicacion("A", 3.4, -76.5);
    private final Ubicacion b = new Ubicacion("B", 3.5, -76.5);

    @Test
    void aplicaElFactorDeCadaNivelDeTrafico() {
        assertThat(new Ruta("r1", a, b, 5, 10, NivelTrafico.BAJO).calcularTiempoEstimado()).isEqualTo(10.0);
        assertThat(new Ruta("r2", a, b, 5, 10, NivelTrafico.MEDIO).calcularTiempoEstimado()).isEqualTo(13.0);
        assertThat(new Ruta("r3", a, b, 5, 10, NivelTrafico.ALTO).calcularTiempoEstimado()).isEqualTo(17.0);
    }

    @Test
    void desactivarDejaLaRutaInactiva() {
        Ruta ruta = new Ruta("r1", a, b, 5, 10, NivelTrafico.BAJO);
        ruta.desactivar();
        assertThat(ruta.isActiva()).isFalse();
    }
}
