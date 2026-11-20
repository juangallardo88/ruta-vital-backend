package co.edu.ucc.rutavital.modelo;

import org.springframework.data.annotation.TypeAlias;

/** Operador: atiende las emergencias. */
@TypeAlias("Operador")
public class Operador extends Usuario {

    public static final String ROL = "OPERADOR";

    public Operador() {
    }

    public Operador(String nombreUsuario, String contrasenaCifrada) {
        super(nombreUsuario, contrasenaCifrada);
    }

    @Override
    public String getRol() {
        return ROL;
    }
}
