package co.edu.ucc.rutavital.config;

import co.edu.ucc.rutavital.dto.ErrorResponse;
import co.edu.ucc.rutavital.excepcion.ConflictoException;
import co.edu.ucc.rutavital.excepcion.RecursoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Manejo centralizado de errores: código HTTP correcto y mensaje en JSON (RNF-07). */
@RestControllerAdvice(basePackages = "co.edu.ucc.rutavital.controller")
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler({ConflictoException.class, DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> conflicto(RuntimeException ex) {
        String mensaje = ex instanceof DuplicateKeyException ? "La placa o el código interno ya están registrados" : ex.getMessage();
        return error(HttpStatus.CONFLICT, mensaje, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> datosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Hay datos inválidos o incompletos", campos);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> peticionInvalida(Exception ex) {
        String mensaje = ex instanceof IllegalArgumentException ? ex.getMessage() : "La petición tiene un formato o un valor no válido";
        return error(HttpStatus.BAD_REQUEST, mensaje, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> noAutenticado(AuthenticationException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> sinPermiso(AccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, "No tiene permiso para realizar esta operación", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaInexistente(NoResourceFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "El recurso solicitado no existe", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorInterno(Exception ex) {
        log.error("Error no controlado", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Intente de nuevo.", null);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus estado, String mensaje, Map<String, String> detalles) {
        return ResponseEntity.status(estado).body(new ErrorResponse(LocalDateTime.now(), estado.value(), mensaje, detalles));
    }
}
