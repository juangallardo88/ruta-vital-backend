package co.edu.ucc.rutavital.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Usuario del sistema. Es abstracta: solo existen Administrador y Operador.
 * Ambos se guardan en la colección "usuarios"; Spring Data agrega el campo _class.
 */
@Document(collection = "usuarios")
public abstract class Usuario {

    private static final PasswordEncoder CODIFICADOR = new BCryptPasswordEncoder();

    @Id
    private String id;

    @Indexed(unique = true)
    private String nombreUsuario;

    /** Contraseña cifrada con BCrypt (RNF-08). */
    private String contrasena;

    protected Usuario() {
    }

    protected Usuario(String nombreUsuario, String contrasenaCifrada) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasenaCifrada;
    }

    /** Patrón Experto: el usuario verifica su propia contraseña. */
    public boolean verificarContrasena(String contrasenaPlana) {
        return contrasenaPlana != null && contrasena != null && CODIFICADOR.matches(contrasenaPlana, contrasena);
    }

    /** Rol del usuario, definido por la subclase. */
    public abstract String getRol();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
