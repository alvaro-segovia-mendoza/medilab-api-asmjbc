package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config.filters.JwtAuthenticationFilter;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configura la seguridad de la aplicación, definiendo autenticación y autorización
 * para diferentes roles de usuario, y gestionando la política de sesiones.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // Activa la seguridad basada en métodos
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura el filtro de seguridad para las solicitudes HTTP, especificando las
     * rutas permitidas y los roles necesarios para acceder a diferentes endpoints.
     *
     * @param http instancia de {@link HttpSecurity} para configurar la seguridad.
     * @return una instancia de {@link SecurityFilterChain} que contiene la configuración de seguridad.
     * @throws Exception si ocurre un error en la configuración de seguridad.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Entrando en el método securityFilterChain");

        // Configuración de seguridad
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Forbidden\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error", "/error/**").permitAll()

                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/profile/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/rutas/activas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/paradas/ruta/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/paradas/activas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/paradas/*/slots-disponibles").authenticated()
                        .requestMatchers("/api/trailers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rutas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/rutas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/rutas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rutas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/paradas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/paradas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/paradas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/paradas/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/citas").hasAnyRole("PACIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/citas/*/confirm").hasAnyRole("TECNICO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/citas/*/cancel").hasAnyRole("TECNICO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/citas/**").hasAnyRole("PACIENTE", "TECNICO", "MEDICO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/citas/**").hasAnyRole("TECNICO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/citas/**").hasRole("ADMIN")

                        .requestMatchers("/api/tecnicos/**").hasAnyRole("TECNICO", "ADMIN")
                        .requestMatchers("/api/medicos/**").hasAnyRole("MEDICO", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/registros-clinicos").hasAnyRole("TECNICO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/registros-clinicos/*/submit-review").hasAnyRole("TECNICO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/registros-clinicos/*/review").hasAnyRole("MEDICO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/registros-clinicos/paciente/*/historial").hasAnyRole("PACIENTE", "MEDICO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/registros-clinicos/**").hasAnyRole("TECNICO", "MEDICO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/historiales-clinicos/pacientes/*").hasAnyRole("PACIENTE", "MEDICO", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura el codificador de contraseñas para cifrar las contraseñas de los usuarios
     * utilizando BCrypt.
     *
     * @return una instancia de {@link PasswordEncoder} que utiliza BCrypt para cifrar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Entrando en el método passwordEncoder");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        logger.info("Saliendo del método passwordEncoder");
        return encoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/login","/logout")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }
}
