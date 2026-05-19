package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Utilidad de apoyo para generar y validar tokens JWT de acceso y refresco.
 */
@Component
public class JwtUtil {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    @Autowired
    private KeyPair jwtKeyPair;

    private static final long JWT_EXPIRATION = 3_600_000L;
    private static final long REFRESH_TOKEN_EXPIRATION = 604_800_000L;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtKeyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(String username, List<String> roles) {
        return generateAccessToken(username, roles);
    }

    public String generateAccessToken(String username, List<String> roles) {
        return generateToken(username, roles, ACCESS_TOKEN_TYPE, JWT_EXPIRATION);
    }

    public String generateRefreshToken(String username, List<String> roles) {
        return generateToken(username, roles, REFRESH_TOKEN_TYPE, REFRESH_TOKEN_EXPIRATION);
    }

    public boolean validateRefreshToken(String token, String username) {
        return validateTokenByType(token, username, REFRESH_TOKEN_TYPE);
    }

    public boolean validateAccessToken(String token, String username) {
        return validateTokenByType(token, username, ACCESS_TOKEN_TYPE);
    }

    public long getAccessTokenExpirationMs() {
        return JWT_EXPIRATION;
    }

    public long getRefreshTokenExpirationMs() {
        return REFRESH_TOKEN_EXPIRATION;
    }

    private String generateToken(String username, List<String> roles, String tokenType, long expirationMs) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(jwtKeyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        return validateAccessToken(token, username);
    }

    private boolean validateTokenByType(String token, String username, String expectedType) {
        try {
            Claims claims = extractAllClaims(token);

            String tokenUsername = claims.getSubject();
            if (tokenUsername == null || !tokenUsername.equals(username)) {
                return false;
            }

            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            if (!expectedType.equals(tokenType)) {
                return false;
            }

            Date exp = claims.getExpiration();
            return exp != null && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
