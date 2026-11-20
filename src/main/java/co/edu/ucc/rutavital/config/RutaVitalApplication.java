package co.edu.ucc.rutavital.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/** Punto de arranque del backend de Ruta Vital. */
@SpringBootApplication(scanBasePackages = "co.edu.ucc.rutavital")
public class RutaVitalApplication {

    public static void main(String[] args) {
        // Las fechas se manejan en hora de Colombia, también en servidores configurados en UTC (Render).
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        SpringApplication.run(RutaVitalApplication.class, args);
    }
}
