package co.edu.ucc.rutavital.modelo;

import co.edu.ucc.rutavital.excepcion.ConflictoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Transiciones de estado y avances de la emergencia (RF-13 a RF-16). */
class EmergenciaTest {

    private Emergencia nueva() {
        return new Emergencia("Accidente", Prioridad.ALTA, TipoAmbulancia.BASICA,
                new Ubicacion("Lugar", 3.4, -76.5), new Ubicacion("Hospital", 3.45, -76.53),
                LocalDateTime.now().minusMinutes(30), "op1");
    }

    private Ambulancia ambulancia() {
        Ambulancia a = new Ambulancia("ABC123", "AMB-01", TipoAmbulancia.BASICA, new Ubicacion("Base", 3.4, -76.5), EstadoAmbulancia.DISPONIBLE);
        a.setId("amb1");
        return a;
    }

    @Test
    void seRegistraEnEstadoRegistrada() {
        assertThat(nueva().getEstado()).isEqualTo(EstadoEmergencia.REGISTRADA);
    }

    @Test
    void asignarLaPoneEnCursoYAceptaRutaNula() {
        Emergencia e = nueva();
        e.asignar(ambulancia(), null);
        assertThat(e.getEstado()).isEqualTo(EstadoEmergencia.EN_CURSO);
        assertThat(e.getAmbulanciaId()).isEqualTo("amb1");
        assertThat(e.getRutaId()).isNull();
    }

    @Test
    void noPermiteFinalizarUnaEmergenciaRegistrada() {
        assertThatThrownBy(() -> nueva().finalizar("Atendido", 20.0)).isInstanceOf(ConflictoException.class);
    }

    @Test
    void losEstadosFinalesNoCambian() {
        Emergencia e = nueva();
        e.cancelar();
        assertThatThrownBy(e::cancelar).isInstanceOf(ConflictoException.class);
        assertThatThrownBy(() -> e.asignar(ambulancia(), null)).isInstanceOf(ConflictoException.class);
    }

    @Test
    void cancelarIndicaSiHayQueLiberarLaAmbulancia() {
        Emergencia registrada = nueva();
        assertThat(registrada.cancelar()).isFalse();
        Emergencia enCurso = nueva();
        enCurso.asignar(ambulancia(), null);
        assertThat(enCurso.cancelar()).isTrue();
    }

    @Test
    void laLlegadaAlCentroRequiereLaLlegadaAlLugar() {
        Emergencia e = nueva();
        e.asignar(ambulancia(), null);
        assertThatThrownBy(() -> e.registrarAvance(TipoAvance.LLEGADA_CENTRO)).isInstanceOf(ConflictoException.class);
        e.registrarAvance(TipoAvance.LLEGADA_LUGAR);
        e.registrarAvance(TipoAvance.LLEGADA_CENTRO);
        assertThat(e.getAvances()).hasSize(2);
        assertThat(e.getEstado()).isEqualTo(EstadoEmergencia.EN_CURSO);
    }

    @Test
    void noSeRegistranAvancesFueraDeCurso() {
        assertThatThrownBy(() -> nueva().registrarAvance(TipoAvance.LLEGADA_LUGAR)).isInstanceOf(ConflictoException.class);
    }

    @Test
    void finalizarCalculaElTiempoConLosAvances() {
        Emergencia e = nueva();
        e.asignar(ambulancia(), null);
        e.registrarAvance(TipoAvance.LLEGADA_LUGAR);
        e.registrarAvance(TipoAvance.LLEGADA_CENTRO);
        e.finalizar("Paciente estable", 99.0);
        assertThat(e.getEstado()).isEqualTo(EstadoEmergencia.FINALIZADA);
        // Se registró hace 30 minutos: el tiempo se calcula con los avances, no con el simulado
        assertThat(e.getTiempoRecorridoMin()).isBetween(29.0, 31.0);
    }

    @Test
    void finalizarUsaElTiempoSimuladoSiNoHayLlegadaAlCentro() {
        Emergencia e = nueva();
        e.asignar(ambulancia(), null);
        e.finalizar("Atendido en sitio", 18.5);
        assertThat(e.getTiempoRecorridoMin()).isEqualTo(18.5);
        assertThat(e.getResultado()).isEqualTo("Atendido en sitio");
    }

    @Test
    void finalizarSinTiempoNiAvancesEsInvalido() {
        Emergencia e = nueva();
        e.asignar(ambulancia(), null);
        assertThatThrownBy(() -> e.finalizar("x", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
