package co.edu.ucc.rutavital.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Indica a Spring Data dónde están los repositorios y los documentos, porque la clase de
 * arranque vive en el paquete config (según la vista de desarrollo). Va en una clase aparte
 * para que las pruebas de controladores (@WebMvcTest) no intenten conectarse a MongoDB.
 */
@Configuration
@EnableMongoRepositories(basePackages = "co.edu.ucc.rutavital.repository")
@EntityScan(basePackages = "co.edu.ucc.rutavital.modelo")
public class ConfiguracionMongo {
}
