package com.openclassrooms.etudiant.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${etudiant-backend.jwtSecret}")
    private String jwtSecret;

    @Value("${etudiant-backend.jwtExpirationTime}")
    private Long jwtExpirationTime;

    /**
     * Generates a key using Base64 encoded JWT secret (defined in environment).
     * @return A key ready to use with {@link Jwts} builder to create JWT
     * @throws io.jsonwebtoken.security.WeakKeyException If the secret is too weak (bit length < 256)
     * @see Keys#hmacShaKeyFor
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Generates a JWT using provided user details (mostly username and password).
     * Its duration is defined in environment.
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

}
