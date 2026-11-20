package co.edu.ucc.rutavital.config;

import co.edu.ucc.rutavital.dto.LoginResponse;
import co.edu.ucc.rutavital.modelo.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Genera los tokens JWT firmados (patrón Fabricación pura). La validación de cada
 * petición la hace el filtro de Spring Security configurado en SecurityConfig.
 */
@Service
public class JwtService {

    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_ID = "uid";

    private final JwtEncoder jwtEncoder;
    private final long horasExpiracion;

    public JwtService(JwtEncoder jwtEncoder, @Value("${app.jwt.expiracion-horas:8}") long horasExpiracion) {
        this.jwtEncoder = jwtEncoder;
        this.horasExpiracion = horasExpiracion;
    }

    public LoginResponse generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(horasExpiracion, ChronoUnit.HOURS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ruta-vital")
                .issuedAt(ahora)
                .expiresAt(expira)
                .subject(usuario.getNombreUsuario())
                .claim(CLAIM_ROL, usuario.getRol())
                .claim(CLAIM_ID, usuario.getId())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse(token, usuario.getNombreUsuario(), usuario.getRol(), expira);
    }
}
