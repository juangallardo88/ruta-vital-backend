package co.edu.ucc.rutavital.repository;

import co.edu.ucc.rutavital.modelo.Ruta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/** Acceso a la colección "rutas". */
public interface RutaRepository extends MongoRepository<Ruta, String> {

    /** Rutas activas entre un origen y un destino, comparando por nombre de lugar (RF-09). */
    @Query("{ 'activa': true, 'origen.nombre': ?0, 'destino.nombre': ?1 }")
    List<Ruta> findActivasEntre(String origen, String destino);
}
