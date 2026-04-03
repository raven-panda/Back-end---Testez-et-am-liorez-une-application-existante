package com.openclassrooms.etudiant.service;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${etudiant-backend.jwtSecret}")
    private String jwtSecret;

    @Value("${etudiant-backend.jwtExpirationTime}")
    private Long jwtExpirationTime;

    /**
     * Generates a key using Base64 encoded JWT secret (defined in environment).
     *
     * @return A key ready to use with {@link Jwts} builder to create JWT
     * @throws io.jsonwebtoken.security.WeakKeyException If the secret is too weak (bit length < 256)
     * @see Keys#hmacShaKeyFor
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Generates a JWT using provided user details (mostly username and password).
     * Its duration is defined in environment.
     *
     * @param userDetails User details used for JWT generation
     * @return Encoded JWT ready to use
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject((userDetails.getUsername()))
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + jwtExpirationTime))
            .signWith(key())
            .compact();
    }

    public boolean validateJwtToken(@Nullable String authToken) {
        if (authToken == null)
            return false;

        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key()).build()
            .parseSignedClaims(token).getPayload().getSubject();
    }

}
