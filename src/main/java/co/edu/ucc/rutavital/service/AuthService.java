package co.edu.ucc.rutavital.service;

import co.edu.ucc.rutavital.config.JwtService;
import co.edu.ucc.rutavital.dto.LoginResponse;
import co.edu.ucc.rutavital.modelo.Usuario;
import co.edu.ucc.rutavital.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/** Autenticación de usuarios (RF-18). */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario.trim())
                .filter(u -> u.verificarContrasena(contrasena))
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));
        return jwtService.generarToken(usuario);
    }
}
