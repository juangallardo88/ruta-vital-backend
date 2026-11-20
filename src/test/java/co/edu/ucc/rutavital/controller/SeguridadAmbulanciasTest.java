package co.edu.ucc.rutavital.controller;

import co.edu.ucc.rutavital.config.ManejadorErrores;
import co.edu.ucc.rutavital.config.RutaVitalApplication;
import co.edu.ucc.rutavital.config.SecurityConfig;
import co.edu.ucc.rutavital.modelo.Ambulancia;
import co.edu.ucc.rutavital.modelo.EstadoAmbulancia;
import co.edu.ucc.rutavital.modelo.TipoAmbulancia;
import co.edu.ucc.rutavital.modelo.Ubicacion;
import co.edu.ucc.rutavital.service.AmbulanciaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** RNF-07 y RNF-08: autenticación obligatoria, permisos por rol y errores en JSON. */
@WebMvcTest(AmbulanciaController.class)
@ContextConfiguration(classes = RutaVitalApplication.class)
@Import({SecurityConfig.class, ManejadorErrores.class})
class SeguridadAmbulanciasTest {

    private static final String JSON_VALIDO = """
            {"placa":"ABC123","codigoInterno":"AMB-01","tipo":"BASICA",
             "ubicacion":{"nombre":"Base","latitud":3.4,"longitud":-76.5},"estado":"DISPONIBLE"}""";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AmbulanciaService ambulanciaService;

    @Test
    void sinTokenRespondeNoAutenticado() throws Exception {
        mvc.perform(get("/api/ambulancias"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value(401));
    }

    @Test
    void elOperadorPuedeConsultarAmbulancias() throws Exception {
        when(ambulanciaService.listar(null, null)).thenReturn(List.of());
        mvc.perform(get("/api/ambulancias").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERADOR"))))
                .andExpect(status().isOk());
    }

    @Test
    void elOperadorNoPuedeRegistrarAmbulancias() throws Exception {
        mvc.perform(post("/api/ambulancias").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERADOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value(403));
    }

    @Test
    void elAdministradorRegistraAmbulancias() throws Exception {
        Ambulancia creada = new Ambulancia("ABC123", "AMB-01", TipoAmbulancia.BASICA, new Ubicacion("Base", 3.4, -76.5), EstadoAmbulancia.DISPONIBLE);
        creada.setId("amb1");
        when(ambulanciaService.registrar(any())).thenReturn(creada);
        mvc.perform(post("/api/ambulancias").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC123"));
    }

    @Test
    void datosInvalidosRespondenCon400YElCampo() throws Exception {
        String sinPlaca = JSON_VALIDO.replace("\"placa\":\"ABC123\",", "");
        mvc.perform(post("/api/ambulancias").contentType(MediaType.APPLICATION_JSON).content(sinPlaca)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.placa").exists());
    }
}
