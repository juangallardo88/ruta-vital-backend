package co.edu.ucc.rutavital.dto;

import java.time.Instant;

/** Respuesta del inicio de sesión con el token JWT. */
public record LoginResponse(String token, String nombreUsuario, String rol, Instant expiraEn) {
}
