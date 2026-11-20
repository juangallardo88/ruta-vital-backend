package co.edu.ucc.rutavital.modelo;

/**
 * Nivel de tráfico de una ruta con su factor multiplicador (RF-08).
 * Bajo x1.0, Medio x1.3, Alto x1.7.
 */
public enum NivelTrafico {
    BAJO(1.0),
    MEDIO(1.3),
    ALTO(1.7);

    private final double factor;

    NivelTrafico(double factor) {
        this.factor = factor;
    }

    public double getFactor() {
        return factor;
    }
}
