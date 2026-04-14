package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import jakarta.servlet.http.HttpServletRequest;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.AuthRefreshRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.AuthRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.AuthResponseDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.PasswordResetDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.PasswordResetRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RegisterRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.PasswordResetToken;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.PasswordResetTokenRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class AuthenticationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${app.auth.reset-token-expiration-minutes:30}")
    private long resetTokenExpirationMinutes;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private AppUrlService appUrlService;

    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizeEmail(authRequest.getEmail()),
                        authRequest.getPassword()
                )
        );

        return buildAuthResponse(authentication.getName(), extractRoles(authentication), "Authentication successful");
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        validateMatchingPasswords(request.getPassword(), request.getConfirmPassword());

        User user = userService.registerPatient(request.getEmail(), request.getPassword());
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();
        return buildAuthResponse(user.getEmail(), roles, "Registration successful");
    }

    public AuthResponseDTO refresh(AuthRefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtUtil.extractUsername(refreshToken);
        User user = userService.getByEmail(username);

        if (!jwtUtil.validateRefreshToken(refreshToken, user.getEmail())) {
            throw new IllegalArgumentException("Refresh token inválido o expirado");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .toList();
        return buildAuthResponse(user.getEmail(), roles, "Token refreshed");
    }

    public void forgotPassword(PasswordResetRequestDTO request, HttpServletRequest httpRequest) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        User user = userService.getByEmail(normalizedEmail);

        String rawToken = generateOpaqueToken();
        LocalDateTime now = LocalDateTime.now();

        passwordResetTokenRepository.invalidateAllActiveTokensForUser(user.getId(), now);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setTokenHash(hashToken(rawToken));
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusMinutes(resetTokenExpirationMinutes));
        token.setRequestIp(httpRequest.getRemoteAddr());
        token.setUserAgent(truncate(httpRequest.getHeader("User-Agent"), 255));
        passwordResetTokenRepository.save(token);

        String resetUrl = appUrlService.buildResetUrl(rawToken);
        String subject = "Medilab - Recuperación de contraseña";
        String body = """
                Hemos recibido una solicitud para restablecer tu contraseña.

                Usa este enlace para continuar:
                %s

                El enlace caduca en %d minutos. Si no solicitaste este cambio, puedes ignorar este correo.
                """.formatted(resetUrl, resetTokenExpirationMinutes);
        mailService.sendText(user.getEmail(), subject, body);
    }

    public void resetPassword(PasswordResetDTO request) {
        validateMatchingPasswords(request.getNewPassword(), request.getConfirmPassword());

        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hashToken(request.getToken()))
                .orElseThrow(() -> new ResourceNotFoundException("passwordResetToken", "token", "provided"));

        if (token.isUsed() || token.isExpired()) {
            throw new IllegalArgumentException("El token de recuperación es inválido o ha caducado");
        }

        userService.updatePassword(token.getUser().getId(), request.getNewPassword());
        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);
    }

    private AuthResponseDTO buildAuthResponse(String username, List<String> roles, String message) {
        return new AuthResponseDTO(
                jwtUtil.generateAccessToken(username, roles),
                jwtUtil.generateRefreshToken(username, roles),
                "Bearer",
                jwtUtil.getAccessTokenExpirationMs(),
                jwtUtil.getRefreshTokenExpirationMs(),
                message
        );
    }

    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();
    }

    private void validateMatchingPasswords(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
