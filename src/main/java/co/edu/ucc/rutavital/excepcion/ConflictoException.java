package co.edu.ucc.rutavital.excepcion;

/**
 * Se lanza ante datos duplicados o cambios de estado no permitidos (HTTP 409).
 */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
