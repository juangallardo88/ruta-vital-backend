package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.dto.RutaRequest;
import co.edu.ucc.rutavital.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.rutavital.modelo.Ruta;
import co.edu.ucc.rutavital.modelo.Ubicacion;
import co.edu.ucc.rutavital.repository.RutaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Lógica de negocio de las rutas (RF-07 a RF-10). */
@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final EstrategiaRuta estrategiaRuta;

    public RutaService(RutaRepository rutaRepository, EstrategiaRuta estrategiaRuta) {
        this.rutaRepository = rutaRepository;
        this.estrategiaRuta = estrategiaRuta;
    }

    public Ruta registrar(RutaRequest datos) {
        validarTrayecto(datos);
        Ruta ruta = new Ruta(datos.nombre().trim(), datos.origen().aModelo(), datos.destino().aModelo(),
                datos.distanciaKm(), datos.tiempoBaseMin(), datos.nivelTrafico());
        return rutaRepository.save(ruta);
    }

    public List<Ruta> listar() {
        return rutaRepository.findAll().stream()
                .sorted(Comparator.comparing(Ruta::getNombre))
                .toList();
    }

    public Ruta obtener(String id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La ruta no existe"));
    }

    public Ruta actualizar(String id, RutaRequest datos) {
        validarTrayecto(datos);
        Ruta ruta = obtener(id);
        ruta.setNombre(datos.nombre().trim());
        ruta.setOrigen(datos.origen().aModelo());
        ruta.setDestino(datos.destino().aModelo());
        ruta.setDistanciaKm(datos.distanciaKm());
        ruta.setTiempoBaseMin(datos.tiempoBaseMin());
        ruta.setNivelTrafico(datos.nivelTrafico());
        return rutaRepository.save(ruta);
    }

    public Ruta desactivar(String id) {
        Ruta ruta = obtener(id);
        ruta.desactivar();
        return rutaRepository.save(ruta);
    }

    /** Ruta activa de menor tiempo estimado entre el origen y el destino (RF-09). */
    public Optional<Ruta> recomendar(String origen, String destino) {
        return estrategiaRuta.elegir(rutaRepository.findActivasEntre(origen, destino));
    }

    /** Rutas activas evaluadas, ordenadas de menor a mayor tiempo estimado (RF-10). */
    public List<Ruta> alternativas(String origen, String destino) {
        return rutaRepository.findActivasEntre(origen, destino).stream()
                .sorted(Comparator.comparingDouble(Ruta::calcularTiempoEstimado))
                .toList();
    }

    /** Lugares que aparecen en las rutas activas, para elegir origen y destino de una emergencia. */
    public List<Ubicacion> lugares() {
        Map<String, Ubicacion> lugares = new LinkedHashMap<>();
        rutaRepository.findAll().stream()
                .filter(Ruta::isActiva)
                .forEach(r -> {
                    lugares.putIfAbsent(r.getOrigen().getNombre(), r.getOrigen());
                    lugares.putIfAbsent(r.getDestino().getNombre(), r.getDestino());
                });
        return lugares.values().stream()
                .sorted(Comparator.comparing(Ubicacion::getNombre))
                .toList();
    }

    private void validarTrayecto(RutaRequest datos) {
        if (datos.origen().nombre().trim().equalsIgnoreCase(datos.destino().nombre().trim())) {
            throw new IllegalArgumentException("El origen y el destino de la ruta deben ser diferentes");
        }
    }
}
