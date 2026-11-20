package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.dto.AmbulanciaRequest;
import co.edu.ucc.rutavital.excepcion.ConflictoException;
import co.edu.ucc.rutavital.excepcion.RecursoNoEncontradoException;
import co.edu.ucc.rutavital.modelo.Ambulancia;
import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import co.edu.ucc.rutavital.modelo.Ubicacion;
import co.edu.ucc.rutavital.repository.AmbulanciaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Lógica de negocio de las ambulancias (RF-01 a RF-06 y RF-12). Patrón Fabricación pura. */
@Service
public class AmbulanciaService {

    /** Ambulancia recomendada con su distancia aproximada al origen. */
    public record AmbulanciaCercana(Ambulancia ambulancia, double distanciaKm) {
    }

    private final AmbulanciaRepository ambulanciaRepository;

    public AmbulanciaService(AmbulanciaRepository ambulanciaRepository) {
        this.ambulanciaRepository = ambulanciaRepository;
    }

    public Ambulancia registrar(AmbulanciaRequest datos) {
        if (datos.estado() == EstadoAmbulancia.EN_SERVICIO) {
            throw new IllegalArgumentException("El estado inicial debe ser Disponible o Fuera de servicio");
        }
        String placa = datos.placaNormalizada();
        String codigo = datos.codigoInterno().trim();
        if (ambulanciaRepository.existsByPlaca(placa) || ambulanciaRepository.existsByCodigoInterno(codigo)) {
            throw new ConflictoException("La placa o el código interno ya están registrados");
        }
        Ambulancia ambulancia = new Ambulancia(placa, codigo, datos.tipo(), datos.ubicacion().aModelo(), datos.estado());
        return ambulanciaRepository.save(ambulancia);
    }

    public Ambulancia obtener(String id) {
        return ambulanciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La ambulancia no existe"));
    }

    /**
     * Lista las ambulancias (RF-02). Con estado DISPONIBLE devuelve solo las activas
     * y disponibles (RF-06). El tipo es un filtro opcional.
     */
    public List<Ambulancia> listar(EstadoAmbulancia estado, TipoAmbulancia tipo) {
        return ambulanciaRepository.findAll().stream()
                .filter(a -> estado == null || (a.getEstado() == estado && (estado != EstadoAmbulancia.DISPONIBLE || a.isActiva())))
                .filter(a -> tipo == null || a.getTipo() == tipo)
                .sorted(Comparator.comparing(Ambulancia::getCodigoInterno))
                .toList();
    }

    public Ambulancia actualizar(String id, AmbulanciaRequest datos) {
        Ambulancia ambulancia = obtener(id);
        String placa = datos.placaNormalizada();
        String codigo = datos.codigoInterno().trim();
        boolean placaRepetida = !placa.equals(ambulancia.getPlaca()) && ambulanciaRepository.existsByPlaca(placa);
        boolean codigoRepetido = !codigo.equals(ambulancia.getCodigoInterno()) && ambulanciaRepository.existsByCodigoInterno(codigo);
        if (placaRepetida || codigoRepetido) {
            throw new ConflictoException("La placa o el código interno ya están registrados");
        }
        ambulancia.setPlaca(placa);
        ambulancia.setCodigoInterno(codigo);
        ambulancia.setTipo(datos.tipo());
        ambulancia.setUbicacion(datos.ubicacion().aModelo());
        if (datos.estado() != ambulancia.getEstado()) {
            ambulancia.cambiarDisponibilidad(datos.estado());
        }
        return ambulanciaRepository.save(ambulancia);
    }

    public Ambulancia cambiarEstado(String id, EstadoAmbulancia estado) {
        Ambulancia ambulancia = obtener(id);
        ambulancia.cambiarDisponibilidad(estado);
        return ambulanciaRepository.save(ambulancia);
    }

    public Ambulancia desactivar(String id) {
        Ambulancia ambulancia = obtener(id);
        ambulancia.desactivar();
        return ambulanciaRepository.save(ambulancia);
    }

    /** Ambulancias activas y disponibles del tipo indicado. */
    public List<Ambulancia> disponiblesPorTipo(TipoAmbulancia tipo) {
        return ambulanciaRepository.findDisponiblesPorTipo(tipo);
    }

    /** Recomienda la ambulancia disponible del tipo requerido más cercana al origen (RF-12). */
    public Optional<AmbulanciaCercana> recomendar(Ubicacion origen, TipoAmbulancia tipo) {
        return disponiblesPorTipo(tipo).stream()
                .map(a -> new AmbulanciaCercana(a, Math.round(a.getUbicacion().distanciaA(origen) * 100.0) / 100.0))
                .min(Comparator.comparingDouble(AmbulanciaCercana::distanciaKm));
    }

    /** Pone la ambulancia En servicio al confirmar la asignación (RF-13). */
    public Ambulancia ponerEnServicio(String id) {
        Ambulancia ambulancia = obtener(id);
        ambulancia.ponerEnServicio();
        return ambulanciaRepository.save(ambulancia);
    }

    /** Libera la ambulancia y, si se indica, actualiza su ubicación registrada (RF-15, RF-16). */
    public Ambulancia liberar(String id, Ubicacion nuevaUbicacion) {
        Ambulancia ambulancia = obtener(id);
        ambulancia.liberar();
        if (nuevaUbicacion != null) {
            ambulancia.setUbicacion(nuevaUbicacion);
        }
        return ambulanciaRepository.save(ambulancia);
    }
}
