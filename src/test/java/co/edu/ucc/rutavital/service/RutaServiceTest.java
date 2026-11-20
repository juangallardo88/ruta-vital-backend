package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.dto.RutaRequest;
import co.edu.ucc.rutavital.dto.UbicacionDTO;
import co.edu.ucc.rutavital.modelo.NivelTrafico;
import co.edu.ucc.rutavital.modelo.Ruta;
import co.edu.ucc.rutavital.modelo.Ubicacion;
import co.edu.ucc.rutavital.repository.RutaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RutaServiceTest {

    @Mock
    private RutaRepository repositorio;

    private RutaService servicio;

    private final Ubicacion a = new Ubicacion("A", 3.4, -76.5);
    private final Ubicacion b = new Ubicacion("B", 3.5, -76.5);

    @BeforeEach
    void preparar() {
        servicio = new RutaService(repositorio, new RutaMenorTiempo());
    }

    @Test
    void alternativasOrdenadasDeMenorAMayorTiempo() {
        Ruta lenta = new Ruta("Lenta", a, b, 4, 10, NivelTrafico.ALTO);   // 17
        Ruta rapida = new Ruta("Rapida", a, b, 6, 12, NivelTrafico.BAJO); // 12
        Ruta media = new Ruta("Media", a, b, 5, 11, NivelTrafico.MEDIO);  // 14.3
        when(repositorio.findActivasEntre("A", "B")).thenReturn(List.of(lenta, rapida, media));

        assertThat(servicio.alternativas("A", "B")).extracting(Ruta::getNombre).containsExactly("Rapida", "Media", "Lenta");
        assertThat(servicio.recomendar("A", "B")).map(Ruta::getNombre).contains("Rapida");
    }

    @Test
    void sinRutasParaElTrayectoNoHayRecomendacion() {
        when(repositorio.findActivasEntre("A", "C")).thenReturn(List.of());
        assertThat(servicio.recomendar("A", "C")).isEmpty();
    }

    @Test
    void rechazaRutaConOrigenIgualAlDestino() {
        UbicacionDTO lugar = new UbicacionDTO("A", 3.4, -76.5);
        RutaRequest datos = new RutaRequest("Circular", lugar, lugar, 3.0, 5.0, NivelTrafico.BAJO);
        assertThatThrownBy(() -> servicio.registrar(datos)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lugaresSinRepetirYSoloDeRutasActivas() {
        Ubicacion c = new Ubicacion("C", 3.6, -76.5);
        Ruta r1 = new Ruta("r1", a, b, 1, 1, NivelTrafico.BAJO);
        Ruta r2 = new Ruta("r2", b, a, 1, 1, NivelTrafico.BAJO);
        Ruta inactiva = new Ruta("r3", a, c, 1, 1, NivelTrafico.BAJO);
        inactiva.desactivar();
        when(repositorio.findAll()).thenReturn(List.of(r1, r2, inactiva));

        assertThat(servicio.lugares()).extracting(Ubicacion::getNombre).containsExactly("A", "B");
    }
}
