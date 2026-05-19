package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.AuthRefreshRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.AuthRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.AuthResponseDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.PasswordResetDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.PasswordResetRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.RegisterRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.PasswordResetToken;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.PasswordResetTokenRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.communication.AppUrlService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.communication.MailService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n.MessageService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Servicio de autenticacion que emite tokens JWT y gestiona el ciclo de recuperacion de contrasena.
 */
@Service
@Transactional
public class AuthenticationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;
    private final AppUrlService appUrlService;
    private final MessageService messageService;

    @Value("${app.auth.reset-token-expiration-minutes:30}")
    private long resetTokenExpirationMinutes;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserService userService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            MailService mailService,
            AppUrlService appUrlService,
            MessageService messageService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailService = mailService;
        this.appUrlService = appUrlService;
        this.messageService = messageService;
    }

    public AuthResponseDTO authenticate(AuthRequestDTO authRequest) {
        logger.info("Autenticando usuario con email={}", authRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizeEmail(authRequest.getEmail()),
                        authRequest.getPassword()
                )
        );

        return buildAuthResponse(authentication.getName(), extractRoles(authentication), "api.auth.authenticateSuccess");
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        logger.info("Registrando paciente con email={}", request.getEmail());
        validateMatchingPasswords(request.getPassword(), request.getConfirmPassword());

        User user = userService.registerPatient(request.getEmail(), request.getPassword());
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();
        return buildAuthResponse(user.getEmail(), roles, "api.auth.registrationSuccess");
    }

    public AuthResponseDTO refresh(AuthRefreshRequestDTO request) {
        logger.info("Renovando access token a partir de refresh token");
        String refreshToken = request.getRefreshToken();
        String username;
        try {
            username = jwtUtil.extractUsername(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Refresh token invalido durante la renovacion");
            throw ApiBusinessException.badRequest("INVALID_REFRESH_TOKEN", "api.error.refreshTokenInvalid");
        }
        User user = userService.getByEmail(username);

        if (!jwtUtil.validateRefreshToken(refreshToken, user.getEmail())) {
            throw ApiBusinessException.badRequest("INVALID_REFRESH_TOKEN", "api.error.refreshTokenInvalid");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .toList();
        return buildAuthResponse(user.getEmail(), roles, "api.auth.refreshSuccess");
    }

    public void forgotPassword(PasswordResetRequestDTO request, HttpServletRequest httpRequest) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        logger.info("Generando solicitud de reset de contrasena para email={}", normalizedEmail);
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
        String subject = messageService.getMessageForRequest(httpRequest, "mail.passwordReset.subject");
        String body = messageService.getMessageForRequest(
                httpRequest,
                "mail.passwordReset.body",
                resetUrl,
                resetTokenExpirationMinutes
        );
        mailService.sendText(user.getEmail(), subject, body);
    }

    public void resetPassword(PasswordResetDTO request) {
        logger.info("Consumiendo token opaco para reset de contrasena");
        validateMatchingPasswords(request.getNewPassword(), request.getConfirmPassword());

        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hashToken(request.getToken()))
                .orElseThrow(() -> new ResourceNotFoundException("passwordResetToken", "token", "provided"));

        if (token.isUsed() || token.isExpired()) {
            logger.warn("Intento de uso de token de reset invalido o expirado");
            throw ApiBusinessException.badRequest("INVALID_PASSWORD_RESET_TOKEN", "api.error.passwordResetTokenInvalid");
        }

        userService.updatePassword(token.getUser().getId(), request.getNewPassword());
        token.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(token);
        logger.info("Contrasena restablecida para userId={}", token.getUser().getId());
    }

    private AuthResponseDTO buildAuthResponse(String username, List<String> roles, String messageKey) {
        return new AuthResponseDTO(
                jwtUtil.generateAccessToken(username, roles),
                jwtUtil.generateRefreshToken(username, roles),
                "Bearer",
                jwtUtil.getAccessTokenExpirationMs(),
                jwtUtil.getRefreshTokenExpirationMs(),
                messageService.getMessage(messageKey)
        );
    }

    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();
    }

    private void validateMatchingPasswords(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw ApiBusinessException.badRequest("PASSWORD_MISMATCH", "api.error.passwordMismatch");
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
            throw new IllegalStateException("api.error.hashUnavailable", e);
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
