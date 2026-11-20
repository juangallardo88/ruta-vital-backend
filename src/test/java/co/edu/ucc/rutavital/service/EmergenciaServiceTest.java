package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.excepcion.ConflictoException;
import co.edu.ucc.rutavital.modelo.Ambulancia;
import co.edu.ucc.rutavital.modelo.Emergencia;
import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import co.edu.ucc.rutavital.modelo.EstadoEmergencia;
import co.edu.ucc.rutavital.modelo.Prioridad;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import co.edu.ucc.rutavital.modelo.Ubicacion;
import co.edu.ucc.rutavital.repository.EmergenciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmergenciaServiceTest {

    @Mock
    private EmergenciaRepository repositorio;
    @Mock
    private AmbulanciaService ambulanciaService;
    @Mock
    private RutaService rutaService;

    @InjectMocks
    private EmergenciaService servicio;

    private final Ubicacion hospital = new Ubicacion("Hospital", 3.45, -76.53);

    private Emergencia emergencia(String id) {
        Emergencia e = new Emergencia("Accidente", Prioridad.ALTA, TipoAmbulancia.BASICA,
                new Ubicacion("Lugar", 3.40, -76.50), hospital, LocalDateTime.now(), "op1");
        e.setId(id);
        return e;
    }

    private Ambulancia ambulancia(EstadoAmbulancia estado) {
        Ambulancia a = new Ambulancia("ABC123", "AMB-01", TipoAmbulancia.BASICA, new Ubicacion("Base", 3.4, -76.5), estado);
        a.setId("amb1");
        return a;
    }

    @Test
    void confirmarAsignacionPoneLaEmergenciaEnCursoYLaAmbulanciaEnServicio() {
        when(repositorio.findById("e1")).thenReturn(Optional.of(emergencia("e1")));
        when(ambulanciaService.obtener("amb1")).thenReturn(ambulancia(EstadoAmbulancia.DISPONIBLE));
        when(repositorio.save(any(Emergencia.class))).thenAnswer(i -> i.getArgument(0));

        Emergencia resultado = servicio.confirmarAsignacion("e1", "amb1", null);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEmergencia.EN_CURSO);
        assertThat(resultado.getRutaId()).isNull();
        verify(ambulanciaService).ponerEnServicio("amb1");
    }

    @Test
    void rechazaAsignarUnaAmbulanciaQueYaNoEstaDisponible() {
        when(repositorio.findById("e1")).thenReturn(Optional.of(emergencia("e1")));
        when(ambulanciaService.obtener("amb1")).thenReturn(ambulancia(EstadoAmbulancia.EN_SERVICIO));

        assertThatThrownBy(() -> servicio.confirmarAsignacion("e1", "amb1", null)).isInstanceOf(ConflictoException.class);
        verify(ambulanciaService, never()).ponerEnServicio(any());
        verify(repositorio, never()).save(any());
    }

    @Test
    void finalizarLiberaLaAmbulanciaEnElDestino() {
        Emergencia e = emergencia("e1");
        e.asignar(ambulancia(EstadoAmbulancia.DISPONIBLE), null);
        when(repositorio.findById("e1")).thenReturn(Optional.of(e));
        when(repositorio.save(any(Emergencia.class))).thenAnswer(i -> i.getArgument(0));

        Emergencia resultado = servicio.finalizar("e1", "Paciente trasladado", 22.0);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEmergencia.FINALIZADA);
        verify(ambulanciaService).liberar("amb1", hospital);
    }

    @Test
    void cancelarEnCursoLiberaLaAmbulanciaSinMoverla() {
        Emergencia e = emergencia("e1");
        e.asignar(ambulancia(EstadoAmbulancia.DISPONIBLE), null);
        when(repositorio.findById("e1")).thenReturn(Optional.of(e));
        when(repositorio.save(any(Emergencia.class))).thenAnswer(i -> i.getArgument(0));

        servicio.cancelar("e1");

        verify(ambulanciaService).liberar(eq("amb1"), isNull());
    }

    @Test
    void cancelarRegistradaNoTocaAmbulancias() {
        when(repositorio.findById("e1")).thenReturn(Optional.of(emergencia("e1")));
        when(repositorio.save(any(Emergencia.class))).thenAnswer(i -> i.getArgument(0));

        servicio.cancelar("e1");

        verify(ambulanciaService, never()).liberar(any(), any());
    }

    @Test
    void historialFiltraPorEstado() {
        Emergencia registrada = emergencia("e1");
        Emergencia cancelada = emergencia("e2");
        cancelada.cancelar();
        when(repositorio.findByEstado(EstadoEmergencia.CANCELADA)).thenReturn(List.of(cancelada));

        assertThat(servicio.historial(null, null, EstadoEmergencia.CANCELADA, null)).containsExactly(cancelada);
        assertThat(registrada.getEstado()).isEqualTo(EstadoEmergencia.REGISTRADA);
    }

    @Test
    void historialRechazaRangoDeFechasInvertido() {
        assertThatThrownBy(() -> servicio.historial(LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 1), null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
