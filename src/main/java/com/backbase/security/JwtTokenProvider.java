package com.backbase.security;

import com.backbase.security.entity.Client;
import com.backbase.security.entity.Scope;
import com.backbase.security.repository.ClientRepository;
import com.backbase.security.service.ClientService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final long validityInMs;

    public JwtTokenProvider(
            ClientService clientService, ClientRepository clientRepository,
            @Value("${security.jwt.expiration-ms:3600000}") long validityInMs
    ) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.validityInMs = validityInMs;
    }

    public String createToken(String clientId, String clientSecret, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);
        Key key = Keys.hmacShaKeyFor(clientSecret.getBytes());
        Client client = clientRepository.findByClientId(clientId).get();
        String scopes = client.getScopes().stream().map(Scope::getName).collect(Collectors.joining(" "));
        return Jwts.builder()
                .setSubject(username)
                .claim("ClientId", clientId)
                .claim("scope",scopes)
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
