package co.edu.ucc.rutavital.repository;

import co.edu.ucc.rutavital.modelo.Emergencia;
import co.edu.ucc.rutavital.modelo.EstadoEmergencia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Acceso a la colección "emergencias". */
public interface EmergenciaRepository extends MongoRepository<Emergencia, String> {

    List<Emergencia> findByEstado(EstadoEmergencia estado);

    List<Emergencia> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Emergencia> findByAmbulanciaId(String ambulanciaId);
}
