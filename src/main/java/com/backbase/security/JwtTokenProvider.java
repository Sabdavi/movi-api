package com.backbase.security;

import com.backbase.security.service.ClientService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final ClientService clientService;
    private final long validityInMs;

    public JwtTokenProvider(
            ClientService clientService,
            @Value("${security.jwt.expiration-ms:3600000}") long validityInMs
    ) {
        this.clientService = clientService;
        this.validityInMs = validityInMs;
    }

    public String createToken(String clientId, String clientSecret, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);
        Key key = Keys.hmacShaKeyFor(clientSecret.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .claim("ClientId", clientId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                    @Override
                    public Key resolveSigningKey(JwsHeader header, Claims claims) {
                        String clientId = claims.get("ClientId", String.class);
                        String clientSecret = clientService.findSecretByClientId(clientId);
                        return Keys.hmacShaKeyFor(clientSecret.getBytes(StandardCharsets.UTF_8));
                    }
                })
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
