package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Manejador de exito para autenticacion OAuth2 y redireccion posterior al frontend.
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User user = userService.getByEmail(email);
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

        String accessToken = jwtUtil.generateAccessToken(email, roles);
        String refreshToken = jwtUtil.generateRefreshToken(email, roles);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(publicBaseUrl + "/oauth2/callback")
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
