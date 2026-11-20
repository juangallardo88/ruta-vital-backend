package co.edu.ucc.rutavital.controller;

import co.edu.ucc.rutavital.dto.AmbulanciaRequest;
import co.edu.ucc.rutavital.dto.AmbulanciaResponse;
import co.edu.ucc.rutavital.dto.EstadoAmbulanciaRequest;
import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import co.edu.ucc.rutavital.service.AmbulanciaService;
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

/** Gestión de ambulancias (RF-01 a RF-06). */
@RestController
@RequestMapping("/api/ambulancias")
public class AmbulanciaController {

    private final AmbulanciaService ambulanciaService;

    public AmbulanciaController(AmbulanciaService ambulanciaService) {
        this.ambulanciaService = ambulanciaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AmbulanciaResponse registrar(@Valid @RequestBody AmbulanciaRequest dto) {
        return AmbulanciaResponse.desde(ambulanciaService.registrar(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public AmbulanciaResponse obtener(@PathVariable String id) {
        return AmbulanciaResponse.desde(ambulanciaService.obtener(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<AmbulanciaResponse> listar(@RequestParam(required = false) EstadoAmbulancia estado,
                                           @RequestParam(required = false) TipoAmbulancia tipo) {
        return ambulanciaService.listar(estado, tipo).stream().map(AmbulanciaResponse::desde).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AmbulanciaResponse actualizar(@PathVariable String id, @Valid @RequestBody AmbulanciaRequest dto) {
        return AmbulanciaResponse.desde(ambulanciaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AmbulanciaResponse cambiarEstado(@PathVariable String id, @Valid @RequestBody EstadoAmbulanciaRequest dto) {
        return AmbulanciaResponse.desde(ambulanciaService.cambiarEstado(id, dto.estado()));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AmbulanciaResponse desactivar(@PathVariable String id) {
        return AmbulanciaResponse.desde(ambulanciaService.desactivar(id));
    }
}
