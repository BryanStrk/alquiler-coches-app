package com.alquiler.coches.bootstrap;

import com.alquiler.coches.entity.Coche;
import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.Role;
import com.alquiler.coches.entity.TipoCombustible;
import com.alquiler.coches.entity.Usuario;
import com.alquiler.coches.repository.CocheRepository;
import com.alquiler.coches.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carga datos de prueba al arrancar, solo si las tablas están vacías.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String PLACEHOLDER = "https://placehold.co/800x600/0a0a0a/ff6b00?text=";

    private final UsuarioRepository usuarioRepository;
    private final CocheRepository cocheRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsuarios();
        seedCoches();
    }

    private void seedUsuarios() {
        if (usuarioRepository.count() > 0) {
            log.debug("Usuarios ya presentes, se omite el seed de usuarios");
            return;
        }
        usuarioRepository.save(Usuario.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@alquiler.com")
                .nombre("Admin")
                .apellidos("Sistema")
                .role(Role.ADMIN)
                .build());
        usuarioRepository.save(Usuario.builder()
                .username("cliente")
                .password(passwordEncoder.encode("cliente123"))
                .email("cliente@test.com")
                .nombre("Bryan")
                .apellidos("Pérez")
                .role(Role.CLIENTE)
                .build());
        log.info("Seed de usuarios completado: admin / cliente");
    }

    private void seedCoches() {
        if (cocheRepository.count() > 0) {
            log.debug("Coches ya presentes, se omite el seed de coches");
            return;
        }

        Coche tesla = Coche.builder()
                .marca("Tesla").modelo("Model 3").matricula("1234 KLM")
                .anio(2024).precioPorDia(new BigDecimal("65.00"))
                .tipoCombustible(TipoCombustible.ELECTRICO).estado(EstadoCoche.DISPONIBLE)
                .kilometros(12000)
                .descripcion("Berlina eléctrica con piloto automático, pantalla central de 15\", "
                        + "acceso sin llave mediante app móvil y autonomía real de 510 km. "
                        + "Carga rápida en Supercharger y actualizaciones de software OTA. "
                        + "Acabado Premium con asientos calefactados y techo panorámico de cristal.")
                .imageUrls(List.of(
                        PLACEHOLDER + "Tesla+Model+3+Frontal",
                        PLACEHOLDER + "Tesla+Model+3+Lateral",
                        PLACEHOLDER + "Tesla+Model+3+Interior"))
                .build();

        Coche bmw = Coche.builder()
                .marca("BMW").modelo("Serie 3 320d").matricula("5678 BNP")
                .anio(2023).precioPorDia(new BigDecimal("80.00"))
                .tipoCombustible(TipoCombustible.DIESEL).estado(EstadoCoche.DISPONIBLE)
                .kilometros(35000)
                .descripcion("Sedán ejecutivo diésel con cambio automático Steptronic de 8 marchas. "
                        + "Maletero amplio de 480 l, navegador profesional y conexión inalámbrica "
                        + "Apple CarPlay/Android Auto. Asientos deportivos en cuero y climatizador "
                        + "bizona. Ideal para viajes largos por su bajo consumo.")
                .imageUrls(List.of(PLACEHOLDER + "BMW+Serie+3"))
                .build();

        Coche toyota = Coche.builder()
                .marca("Toyota").modelo("Corolla Hybrid").matricula("9012 CDR")
                .anio(2024).precioPorDia(new BigDecimal("50.00"))
                .tipoCombustible(TipoCombustible.HIBRIDO).estado(EstadoCoche.DISPONIBLE)
                .kilometros(8000)
                .descripcion("Compacto híbrido autorrecargable con consumo medio de 4,2 l/100 km. "
                        + "Pantalla táctil de 10,5\", cámara de visión trasera y suite completa "
                        + "Toyota Safety Sense. Maletero familiar y mantenimiento muy económico. "
                        + "Perfecto para ciudad gracias a su modo 100% eléctrico a baja velocidad.")
                .imageUrls(List.of(PLACEHOLDER + "Toyota+Corolla+Hybrid"))
                .build();

        Coche audi = Coche.builder()
                .marca("Audi").modelo("A4").matricula("3456 DST")
                .anio(2022).precioPorDia(new BigDecimal("90.00"))
                .tipoCombustible(TipoCombustible.GASOLINA).estado(EstadoCoche.ALQUILADO)
                .kilometros(45000)
                .descripcion("Sedán premium gasolina con acabado S line y faros Matrix LED. "
                        + "Virtual Cockpit de 12,3\", tapicería de cuero y suspensión deportiva. "
                        + "Sonido Bang & Olufsen y asientos delanteros calefactados. "
                        + "Conducción refinada y excelente insonorización en autopista.")
                .imageUrls(List.of(PLACEHOLDER + "Audi+A4"))
                .build();

        Coche renault = Coche.builder()
                .marca("Renault").modelo("Clio").matricula("7890 FGH")
                .anio(2023).precioPorDia(new BigDecimal("35.00"))
                .tipoCombustible(TipoCombustible.GASOLINA).estado(EstadoCoche.DISPONIBLE)
                .kilometros(22000)
                .descripcion("Utilitario ágil y económico, ideal para ciudad y desplazamientos cortos. "
                        + "Pantalla EASY LINK de 7\", sensores de aparcamiento traseros y climatizador. "
                        + "Bajo consumo, fácil de aparcar y maletero sorprendentemente amplio para su "
                        + "segmento. Una opción perfecta para conductores noveles.")
                .imageUrls(List.of(PLACEHOLDER + "Renault+Clio"))
                .build();

        Coche porsche = Coche.builder()
                .marca("Porsche").modelo("911 Carrera").matricula("0011 GTR")
                .anio(2024).precioPorDia(new BigDecimal("250.00"))
                .tipoCombustible(TipoCombustible.GASOLINA).estado(EstadoCoche.MANTENIMIENTO)
                .kilometros(5000)
                .descripcion("Deportivo icónico con motor bóxer 6 cilindros y cambio PDK de 8 marchas. "
                        + "0 a 100 km/h en 4,2 s, modo Sport Chrono y escape deportivo. "
                        + "Interior de cuero exclusivo, asientos deportivos adaptativos y sistema PASM. "
                        + "Una experiencia de conducción única reservada para ocasiones especiales.")
                .imageUrls(List.of(PLACEHOLDER + "Porsche+911+Carrera"))
                .build();

        cocheRepository.saveAll(List.of(tesla, bmw, toyota, audi, renault, porsche));
        log.info("Seed de coches completado: 6 vehículos cargados");
    }
}
