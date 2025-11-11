package org.kenne.app_asymetry_sec.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private static final String TOKEN_TYPE = "token_type";

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    @Value(value = "${app.security.jwt.access-token-expiration:3600}")
    private long accessTokenExpiration; // 1 hour in seconds

    @Value(value = "${app.security.jwt.refresh-token-expiration:604800}")
    private long refreshTokenExpiration; // 7 days in seconds

    public JwtService() throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey("keys/local-only/private_key.pem");
        this.publicKey = KeyUtils.loadPublicKey("keys/local-only/public_key.pem");
    }

    public @NonNull String generateAccessToken(@NonNull String userName) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "ACCESS_TOKEN");

        return buildToken(userName, claims, this.accessTokenExpiration); // Placeholder return
    }

    public @NonNull String generateRefreshToken(@NonNull String userName) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "REFRESH_TOKEN");

        return buildToken(userName, claims, this.refreshTokenExpiration);
    }

    private @NonNull String buildToken(
            @NonNull String userName,
            @NonNull Map<String, Object> claims,
            long expiration
    ) {
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)
                .compact();
    }

    public boolean isTokenValid(@NonNull String token, final @NonNull String expectedUserName) {
        final String userName = extractUsername(token);

        return (userName.equals(expectedUserName) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(@NonNull String token) {
        return extractClaims(token).getExpiration()
                .before(new Date());
    }

    public String extractUsername(@NonNull String token) {
        return extractClaims(token).getSubject();
    }

    private @NonNull Claims extractClaims(@NonNull String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public @NonNull String refreshAccessToken(@NonNull String refreshToken) {
        final Claims claims = extractClaims(refreshToken);

        if (!claims.get(TOKEN_TYPE).equals("REFRESH_TOKEN")) {
            throw new RuntimeException("Invalid token type");
        }

        if (isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token is expired");
        }

        final String userName = claims.getSubject();

        return generateAccessToken(userName);
    }
}
