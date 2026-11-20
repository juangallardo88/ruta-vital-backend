package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.dto.AmbulanciaRequest;
import co.edu.ucc.rutavital.dto.UbicacionDTO;
import co.edu.ucc.rutavital.excepcion.ConflictoException;
import co.edu.ucc.rutavital.modelo.Ambulancia;
import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import co.edu.ucc.rutavital.modelo.Ubicacion;
import co.edu.ucc.rutavital.repository.AmbulanciaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmbulanciaServiceTest {

    @Mock
    private AmbulanciaRepository repositorio;

    @InjectMocks
    private AmbulanciaService servicio;

    private AmbulanciaRequest solicitud(String placa, EstadoAmbulancia estado) {
        return new AmbulanciaRequest(placa, "AMB-01", TipoAmbulancia.BASICA, new UbicacionDTO("Base Norte", 3.48, -76.52), estado);
    }

    @Test
    void registraConPlacaNormalizada() {
        when(repositorio.existsByPlaca("ABC123")).thenReturn(false);
        when(repositorio.existsByCodigoInterno("AMB-01")).thenReturn(false);
        when(repositorio.save(any(Ambulancia.class))).thenAnswer(i -> i.getArgument(0));

        Ambulancia creada = servicio.registrar(solicitud("abc-123", EstadoAmbulancia.DISPONIBLE));

        assertThat(creada.getPlaca()).isEqualTo("ABC123");
        assertThat(creada.isActiva()).isTrue();
    }

    @Test
    void rechazaPlacaDuplicada() {
        when(repositorio.existsByPlaca("ABC123")).thenReturn(true);
        assertThatThrownBy(() -> servicio.registrar(solicitud("ABC123", EstadoAmbulancia.DISPONIBLE)))
                .isInstanceOf(ConflictoException.class);
        verify(repositorio, never()).save(any());
    }

    @Test
    void rechazaEstadoInicialEnServicio() {
        assertThatThrownBy(() -> servicio.registrar(solicitud("ABC123", EstadoAmbulancia.EN_SERVICIO)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void recomiendaLaMasCercanaAlOrigen() {
        Ambulancia lejos = new Ambulancia("AAA111", "A1", TipoAmbulancia.BASICA, new Ubicacion("Lejos", 3.60, -76.50), EstadoAmbulancia.DISPONIBLE);
        Ambulancia cerca = new Ambulancia("BBB222", "A2", TipoAmbulancia.BASICA, new Ubicacion("Cerca", 3.41, -76.50), EstadoAmbulancia.DISPONIBLE);
        when(repositorio.findDisponiblesPorTipo(TipoAmbulancia.BASICA)).thenReturn(List.of(lejos, cerca));

        Optional<AmbulanciaService.AmbulanciaCercana> recomendada =
                servicio.recomendar(new Ubicacion("Emergencia", 3.40, -76.50), TipoAmbulancia.BASICA);

        assertThat(recomendada).isPresent();
        assertThat(recomendada.get().ambulancia().getPlaca()).isEqualTo("BBB222");
        assertThat(recomendada.get().distanciaKm()).isLessThan(2.0);
    }

    @Test
    void sinAmbulanciasDelTipoNoHayRecomendacion() {
        when(repositorio.findDisponiblesPorTipo(TipoAmbulancia.MEDICALIZADA)).thenReturn(List.of());
        assertThat(servicio.recomendar(new Ubicacion("X", 3.4, -76.5), TipoAmbulancia.MEDICALIZADA)).isEmpty();
    }

    @Test
    void listarDisponiblesExcluyeLasInactivas() {
        Ambulancia activa = new Ambulancia("AAA111", "A1", TipoAmbulancia.BASICA, new Ubicacion("B", 3.4, -76.5), EstadoAmbulancia.DISPONIBLE);
        Ambulancia inactiva = new Ambulancia("BBB222", "A2", TipoAmbulancia.BASICA, new Ubicacion("B", 3.4, -76.5), EstadoAmbulancia.DISPONIBLE);
        inactiva.desactivar();
        when(repositorio.findAll()).thenReturn(List.of(activa, inactiva));

        assertThat(servicio.listar(EstadoAmbulancia.DISPONIBLE, null)).containsExactly(activa);
    }
}
