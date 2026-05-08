package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config.filters.JwtAuthenticationFilter;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config.security.RestAccessDeniedHandler;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config.security.RestAuthenticationEntryPoint;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.auth.GithubOAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    private static final List<String> DEFAULT_ALLOWED_HEADERS = List.of("Authorization", "Content-Type", "Accept", "Accept-Language", "Origin", "X-Requested-With");

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    private GithubOAuth2UserService githubOAuth2UserService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          RestAccessDeniedHandler restAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
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
    @Order(3)
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        logger.info("Entrando en el método securityFilterChain");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error", "/error/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
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
                        .requestMatchers(HttpMethod.POST, "/api/citas/*/cancel").hasAnyRole("PACIENTE", "TECNICO", "ADMIN")
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

                        .requestMatchers(HttpMethod.GET, "/api/reservas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/**").hasRole("ADMIN")

                        .anyRequest().hasRole("ADMIN")
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
    public SecurityFilterChain oauth2Chain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(info -> info.userService(githubOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureUrl(publicBaseUrl + "/login?error=oauth2")
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
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
}
