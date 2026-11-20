package co.edu.ucc.rutavital.repository;

import co.edu.ucc.rutavital.modelo.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/** Acceso a la colección "usuarios" (patrones Indirección y Fabricación pura). */
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
