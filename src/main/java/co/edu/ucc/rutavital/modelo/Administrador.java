package co.edu.ucc.rutavital.modelo;

import org.springframework.data.annotation.TypeAlias;

/** Administrador: gestiona ambulancias y rutas. */
@TypeAlias("Administrador")
public class Administrador extends Usuario {

    public static final String ROL = "ADMINISTRADOR";

    public Administrador() {
    }

    public Administrador(String nombreUsuario, String contrasenaCifrada) {
        super(nombreUsuario, contrasenaCifrada);
    }

    @Override
    public String getRol() {
        return ROL;
    }
}
