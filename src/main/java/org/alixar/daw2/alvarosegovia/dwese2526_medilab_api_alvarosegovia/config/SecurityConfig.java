package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ApiErrorDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config.filters.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Configura la seguridad de la aplicación, definiendo autenticación y autorización
 * para diferentes roles de usuario, y gestionando la política de sesiones.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // Activa la seguridad basada en métodos
@EnableWebSecurity
public class SecurityConfig {

    private static final List<String> DEFAULT_ALLOWED_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private static final List<String> DEFAULT_ALLOWED_HEADERS = List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With");

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource,
            ObjectMapper objectMapper
    ) throws Exception {
        logger.info("Entrando en el método securityFilterChain");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeApiError(
                                        response,
                                        ApiErrorDTO.basic(
                                                HttpStatus.UNAUTHORIZED.value(),
                                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                                "Debes autenticarte para acceder a este recurso",
                                                request.getRequestURI()
                                        ),
                                        objectMapper
                                ))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeApiError(
                                        response,
                                        ApiErrorDTO.basic(
                                                HttpStatus.FORBIDDEN.value(),
                                                HttpStatus.FORBIDDEN.getReasonPhrase(),
                                                "No tienes permisos para acceder a este recurso",
                                                request.getRequestURI()
                                        ),
                                        objectMapper
                                ))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error", "/error/**").permitAll()
                        .requestMatchers("/", "/login", "/logout").permitAll()
                        .requestMatchers("/users/**").authenticated()

                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
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

                        .anyRequest().permitAll()
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
    public SecurityFilterChain swaggerChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .securityMatcher(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/login","/logout")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(DEFAULT_ALLOWED_METHODS);
        configuration.setAllowedHeaders(DEFAULT_ALLOWED_HEADERS);
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }

    private void writeApiError(HttpServletResponse response, ApiErrorDTO body, ObjectMapper objectMapper) throws IOException {
        response.setStatus(body.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
