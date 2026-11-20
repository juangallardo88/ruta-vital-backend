package co.edu.ucc.rutavital.controller;

import co.edu.ucc.rutavital.dto.RutaRequest;
import co.edu.ucc.rutavital.dto.RutaResponse;
import co.edu.ucc.rutavital.dto.UbicacionDTO;
import co.edu.ucc.rutavital.modelo.Ruta;
import co.edu.ucc.rutavital.service.RutaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Gestión y selección de rutas (RF-07 a RF-10). */
@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public RutaResponse registrar(@Valid @RequestBody RutaRequest dto) {
        return RutaResponse.desde(rutaService.registrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<RutaResponse> listar() {
        return rutaService.listar().stream().map(RutaResponse::desde).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public RutaResponse actualizar(@PathVariable String id, @Valid @RequestBody RutaRequest dto) {
        return RutaResponse.desde(rutaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public RutaResponse desactivar(@PathVariable String id) {
        return RutaResponse.desde(rutaService.desactivar(id));
    }

    /** Rutas evaluadas entre dos lugares, con la recomendada marcada (RF-09, RF-10). */
    @GetMapping("/alternativas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<RutaResponse> alternativas(@RequestParam String origen, @RequestParam String destino) {
        String recomendada = rutaService.recomendar(origen, destino).map(Ruta::getId).orElse(null);
        return rutaService.alternativas(origen, destino).stream()
                .map(r -> RutaResponse.desde(r, r.getId().equals(recomendada)))
                .toList();
    }

    /** Lugares disponibles para el origen y el destino de una emergencia. */
    @GetMapping("/lugares")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<UbicacionDTO> lugares() {
        return rutaService.lugares().stream().map(UbicacionDTO::desde).toList();
    }
}
