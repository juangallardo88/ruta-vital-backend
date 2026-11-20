package co.edu.ucc.rutavital.controller;

import co.edu.ucc.rutavital.config.JwtService;
import co.edu.ucc.rutavital.dto.AsignacionRequest;
import co.edu.ucc.rutavital.dto.AvanceRequest;
import co.edu.ucc.rutavital.dto.EmergenciaRequest;
import co.edu.ucc.rutavital.dto.EmergenciaResponse;
import co.edu.ucc.rutavital.dto.FinalizacionRequest;
import co.edu.ucc.rutavital.dto.RecomendacionResponse;
import co.edu.ucc.rutavital.modelo.EstadoEmergencia;
import co.edu.ucc.rutavital.service.EmergenciaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Gestión y seguimiento de emergencias (RF-11 a RF-17). */
@RestController
@RequestMapping("/api/emergencias")
public class EmergenciaController {

    private final EmergenciaService emergenciaService;

    public EmergenciaController(EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('OPERADOR')")
    public EmergenciaResponse registrar(@Valid @RequestBody EmergenciaRequest dto, @AuthenticationPrincipal Jwt usuario) {
        String operadorId = usuario.getClaimAsString(JwtService.CLAIM_ID);
        return emergenciaService.aRespuesta(emergenciaService.registrar(dto, operadorId));
    }

    @GetMapping("/{id}/recomendacion")
    @PreAuthorize("hasRole('OPERADOR')")
    public RecomendacionResponse recomendar(@PathVariable String id) {
        return emergenciaService.recomendar(id);
    }

    @PatchMapping("/{id}/asignacion")
    @PreAuthorize("hasRole('OPERADOR')")
    public EmergenciaResponse asignar(@PathVariable String id, @Valid @RequestBody AsignacionRequest dto) {
        return emergenciaService.aRespuesta(emergenciaService.confirmarAsignacion(id, dto.ambulanciaId(), dto.rutaId()));
    }

    @PostMapping("/{id}/avances")
    @PreAuthorize("hasRole('OPERADOR')")
    public EmergenciaResponse registrarAvance(@PathVariable String id, @Valid @RequestBody AvanceRequest dto) {
        return emergenciaService.aRespuesta(emergenciaService.registrarAvance(id, dto.tipo()));
    }

    @PatchMapping("/{id}/finalizacion")
    @PreAuthorize("hasRole('OPERADOR')")
    public EmergenciaResponse finalizar(@PathVariable String id, @Valid @RequestBody FinalizacionRequest dto) {
        return emergenciaService.aRespuesta(emergenciaService.finalizar(id, dto.resultado(), dto.tiempoRecorridoMin()));
    }

    @PatchMapping("/{id}/cancelacion")
    @PreAuthorize("hasRole('OPERADOR')")
    public EmergenciaResponse cancelar(@PathVariable String id) {
        return emergenciaService.aRespuesta(emergenciaService.cancelar(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    public List<EmergenciaResponse> historial(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) EstadoEmergencia estado,
            @RequestParam(required = false) String ambulanciaId) {
        return emergenciaService.historial(desde, hasta, estado, ambulanciaId).stream()
                .map(emergenciaService::aRespuesta).toList();
    }
}
