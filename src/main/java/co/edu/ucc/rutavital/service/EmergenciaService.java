package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.dto.AmbulanciaResponse;
import co.edu.ucc.rutavital.dto.EmergenciaRequest;
import co.edu.ucc.rutavital.dto.EmergenciaResponse;
import co.edu.ucc.rutavital.dto.RecomendacionResponse;
import co.edu.ucc.rutavital.dto.RutaResponse;
import co.edu.ucc.rutavital.excepcion.ConflictoException;
import co.edu.ucc.rutavital.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.rutavital.modelo.Ambulancia;
import co.edu.ucc.rutavital.modelo.Emergencia;
import co.edu.ucc.rutavital.modelo.EstadoEmergencia;
import co.edu.ucc.rutavital.modelo.Ruta;
import co.edu.ucc.rutavital.modelo.TipoAvance;
import co.edu.ucc.rutavital.repository.EmergenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Lógica de negocio de las emergencias (RF-11 a RF-17). Coordina los servicios de
 * ambulancias y rutas sin conocer cómo se guardan sus datos (bajo acoplamiento).
 */
@Service
public class EmergenciaService {

    private final EmergenciaRepository emergenciaRepository;
    private final AmbulanciaService ambulanciaService;
    private final RutaService rutaService;

    public EmergenciaService(EmergenciaRepository emergenciaRepository, AmbulanciaService ambulanciaService, RutaService rutaService) {
        this.emergenciaRepository = emergenciaRepository;
        this.ambulanciaService = ambulanciaService;
        this.rutaService = rutaService;
    }

    public Emergencia registrar(EmergenciaRequest datos, String operadorId) {
        if (datos.origen().nombre().trim().equalsIgnoreCase(datos.destino().nombre().trim())) {
            throw new IllegalArgumentException("El origen y el destino deben ser diferentes");
        }
        Emergencia emergencia = new Emergencia(datos.descripcion().trim(), datos.prioridad(), datos.tipoRequerido(),
                datos.origen().aModelo(), datos.destino().aModelo(), datos.fechaHora(), operadorId);
        return emergenciaRepository.save(emergencia);
    }

    public Emergencia obtener(String id) {
        return emergenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La emergencia no existe"));
    }

    /** Recomienda ambulancia (RF-12) y ruta (RF-09, RF-10) para una emergencia registrada. */
    public RecomendacionResponse recomendar(String id) {
        Emergencia emergencia = obtener(id);
        if (emergencia.getEstado() != EstadoEmergencia.REGISTRADA) {
            throw new ConflictoException("Solo se pueden pedir recomendaciones para emergencias registradas");
        }
        Optional<AmbulanciaService.AmbulanciaCercana> cercana =
                ambulanciaService.recomendar(emergencia.getOrigen(), emergencia.getTipoRequerido());
        List<AmbulanciaResponse> disponibles = ambulanciaService.disponiblesPorTipo(emergencia.getTipoRequerido())
                .stream().map(AmbulanciaResponse::desde).toList();

        String origen = emergencia.getOrigen().getNombre();
        String destino = emergencia.getDestino().getNombre();
        List<Ruta> rutas = rutaService.alternativas(origen, destino);
        String recomendadaId = rutaService.recomendar(origen, destino).map(Ruta::getId).orElse(null);
        List<RutaResponse> rutasEvaluadas = rutas.stream()
                .map(r -> RutaResponse.desde(r, r.getId().equals(recomendadaId)))
                .toList();

        return new RecomendacionResponse(
                cercana.map(c -> AmbulanciaResponse.desde(c.ambulancia())).orElse(null),
                cercana.map(AmbulanciaService.AmbulanciaCercana::distanciaKm).orElse(null),
                cercana.isPresent() ? "Distancia aproximada en línea recta" : "No hay ambulancias disponibles del tipo requerido",
                disponibles,
                rutasEvaluadas,
                rutas.isEmpty() ? "No es posible recomendar una ruta: no hay rutas registradas para este trayecto" : null);
    }

    /** Confirma la asignación de ambulancia y ruta (RF-13). */
    public Emergencia confirmarAsignacion(String id, String ambulanciaId, String rutaId) {
        Emergencia emergencia = obtener(id);
        Ambulancia ambulancia = ambulanciaService.obtener(ambulanciaId);
        if (!ambulancia.estaDisponible()) {
            throw new ConflictoException("La ambulancia elegida ya no está disponible");
        }
        Ruta ruta = (rutaId == null || rutaId.isBlank()) ? null : rutaService.obtener(rutaId);
        emergencia.asignar(ambulancia, ruta);
        ambulanciaService.ponerEnServicio(ambulanciaId);
        return emergenciaRepository.save(emergencia);
    }

    /** Registra la llegada al lugar o al centro de atención (RF-14). */
    public Emergencia registrarAvance(String id, TipoAvance tipo) {
        Emergencia emergencia = obtener(id);
        emergencia.registrarAvance(tipo);
        return emergenciaRepository.save(emergencia);
    }

    /** Finaliza la emergencia, guarda el resultado y libera la ambulancia en el destino (RF-16). */
    public Emergencia finalizar(String id, String resultado, Double tiempoSimuladoMin) {
        Emergencia emergencia = obtener(id);
        emergencia.finalizar(resultado.trim(), tiempoSimuladoMin);
        Emergencia guardada = emergenciaRepository.save(emergencia);
        ambulanciaService.liberar(emergencia.getAmbulanciaId(), emergencia.getDestino());
        return guardada;
    }

    /** Cancela la emergencia y libera la ambulancia si estaba en curso (RF-15). */
    public Emergencia cancelar(String id) {
        Emergencia emergencia = obtener(id);
        boolean liberarAmbulancia = emergencia.cancelar();
        Emergencia guardada = emergenciaRepository.save(emergencia);
        if (liberarAmbulancia) {
            ambulanciaService.liberar(emergencia.getAmbulanciaId(), null);
        }
        return guardada;
    }

    /** Historial con filtros opcionales por rango de fechas, estado y ambulancia (RF-17). */
    public List<Emergencia> historial(LocalDate desde, LocalDate hasta, EstadoEmergencia estado, String ambulanciaId) {
        LocalDateTime inicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fin = hasta != null ? hasta.plusDays(1).atStartOfDay() : null;
        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
        }
        List<Emergencia> base;
        if (inicio != null && fin != null) {
            base = emergenciaRepository.findByFechaHoraBetween(inicio, fin);
        } else if (estado != null) {
            base = emergenciaRepository.findByEstado(estado);
        } else if (ambulanciaId != null && !ambulanciaId.isBlank()) {
            base = emergenciaRepository.findByAmbulanciaId(ambulanciaId);
        } else {
            base = emergenciaRepository.findAll();
        }
        Predicate<Emergencia> filtro = e -> (inicio == null || !e.getFechaHora().isBefore(inicio))
                && (fin == null || e.getFechaHora().isBefore(fin))
                && (estado == null || e.getEstado() == estado)
                && (ambulanciaId == null || ambulanciaId.isBlank() || ambulanciaId.equals(e.getAmbulanciaId()));
        return base.stream()
                .filter(filtro)
                .sorted(Comparator.comparing(Emergencia::getFechaHora).reversed())
                .toList();
    }

    /** Convierte una emergencia a DTO con la placa de la ambulancia y el nombre de la ruta. */
    public EmergenciaResponse aRespuesta(Emergencia e) {
        String placa = e.getAmbulanciaId() == null ? null : buscarPlaca(e.getAmbulanciaId());
        String ruta = e.getRutaId() == null ? null : buscarNombreRuta(e.getRutaId());
        return EmergenciaResponse.desde(e, placa, ruta);
    }

    private String buscarPlaca(String ambulanciaId) {
        try {
            return ambulanciaService.obtener(ambulanciaId).getPlaca();
        } catch (RecursoNoEncontradoException ex) {
            return null;
        }
    }

    private String buscarNombreRuta(String rutaId) {
        try {
            return rutaService.obtener(rutaId).getNombre();
        } catch (RecursoNoEncontradoException ex) {
            return null;
        }
    }
}
