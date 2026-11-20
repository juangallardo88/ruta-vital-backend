package co.edu.ucc.rutavital.repository;

import co.edu.ucc.rutavital.modelo.Ambulancia;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/** Acceso a la colección "ambulancias". */
public interface AmbulanciaRepository extends MongoRepository<Ambulancia, String> {

    /** Ambulancias activas, disponibles y del tipo indicado (RF-06, RF-12). */
    @Query("{ 'activa': true, 'estado': 'DISPONIBLE', 'tipo': ?0 }")
    List<Ambulancia> findDisponiblesPorTipo(TipoAmbulancia tipo);

    boolean existsByPlaca(String placa);

    boolean existsByCodigoInterno(String codigoInterno);
}
