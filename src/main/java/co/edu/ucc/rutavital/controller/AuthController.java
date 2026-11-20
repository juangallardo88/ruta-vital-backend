package co.edu.ucc.rutavital.controller;

import co.edu.ucc.rutavital.dto.LoginRequest;
import co.edu.ucc.rutavital.dto.LoginResponse;
import co.edu.ucc.rutavital.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Inicio de sesión (RF-18). Patrón Controlador. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest credenciales) {
        return authService.autenticar(credenciales.nombreUsuario(), credenciales.contrasena());
    }
}
