package com.portfolio.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.function.Function;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private String SECRET_KEY = "c2VjdXJlLXN1cGVyLXN0cm9uZy1sZW5ndGgtc2VjcmV0LWtleS1mb3ItSFM1MTItdXNlci1hdXRoZW50aWNhdGlvbi1zZWN1cml0eQ==";// Base64 encoded secret key
    private final long EXPIRATION_TIME = 60 * 60 * 24; //a day

    // Get the signing key for JWT (decode Base64 if necessary)
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode the Base64 encoded secret key
        return Keys.hmacShaKeyFor(keyBytes); // Use the decoded key to generate signing key
    }

    // Generate token with username and roles
    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)  // Store username in token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)  // Use the signing key
                .compact();
        return token;
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Use the correct signing key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validate token and check if it's expired
    public boolean validateToken(String token) {
        try {
            String username = extractUsername(token);
            return username != null && !isTokenExpired(token);
        } catch (JwtException e) {
            return false; // Invalid token
        }
    }
    
    // Check if the token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateTokenWithUserDetails(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
