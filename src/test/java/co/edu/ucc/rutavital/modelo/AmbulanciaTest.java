package co.edu.ucc.rutavital.modelo;

import co.edu.ucc.rutavital.excepcion.ConflictoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Reglas de disponibilidad de la ambulancia (RF-04, RF-05, RF-13, RF-16). */
class AmbulanciaTest {

    private Ambulancia nueva(EstadoAmbulancia estado) {
        return new Ambulancia("ABC123", "AMB-01", TipoAmbulancia.BASICA, new Ubicacion("Base", 3.4, -76.5), estado);
    }

    @Test
    void soloEstaDisponibleSiEstaActivaYDisponible() {
        Ambulancia a = nueva(EstadoAmbulancia.DISPONIBLE);
        assertThat(a.estaDisponible()).isTrue();
        a.desactivar();
        assertThat(a.estaDisponible()).isFalse();
        assertThat(nueva(EstadoAmbulancia.FUERA_DE_SERVICIO).estaDisponible()).isFalse();
    }

    @Test
    void ponerEnServicioYLiberar() {
        Ambulancia a = nueva(EstadoAmbulancia.DISPONIBLE);
        a.ponerEnServicio();
        assertThat(a.getEstado()).isEqualTo(EstadoAmbulancia.EN_SERVICIO);
        a.liberar();
        assertThat(a.getEstado()).isEqualTo(EstadoAmbulancia.DISPONIBLE);
    }

    @Test
    void noSePuedePonerEnServicioSiNoEstaDisponible() {
        Ambulancia a = nueva(EstadoAmbulancia.FUERA_DE_SERVICIO);
        assertThatThrownBy(a::ponerEnServicio).isInstanceOf(ConflictoException.class);
    }

    @Test
    void elAdministradorNoPuedeAsignarEnServicioManualmente() {
        Ambulancia a = nueva(EstadoAmbulancia.DISPONIBLE);
        assertThatThrownBy(() -> a.cambiarDisponibilidad(EstadoAmbulancia.EN_SERVICIO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void noSePuedeCambiarNiDesactivarUnaAmbulanciaEnServicio() {
        Ambulancia a = nueva(EstadoAmbulancia.DISPONIBLE);
        a.ponerEnServicio();
        assertThatThrownBy(() -> a.cambiarDisponibilidad(EstadoAmbulancia.FUERA_DE_SERVICIO))
                .isInstanceOf(ConflictoException.class);
        assertThatThrownBy(a::desactivar).isInstanceOf(ConflictoException.class);
    }
}
