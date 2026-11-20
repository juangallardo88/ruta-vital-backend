package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.modelo.Ruta;

import java.util.List;
import java.util.Optional;

/**
 * Criterio para elegir la ruta recomendada (patrones Polimorfismo y Variaciones protegidas,
 * patrón de diseño Strategy). Permite cambiar el criterio sin modificar RutaService.
 */
public interface EstrategiaRuta {
    Optional<Ruta> elegir(List<Ruta> rutas);
}
