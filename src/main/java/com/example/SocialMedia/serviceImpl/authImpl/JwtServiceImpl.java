package com.example.SocialMedia.serviceImpl.authImpl;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.token.ttl:86400000}")
    private long accessExpiration;

    private  <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }
    @Override
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }
    @Override
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractClaims(token, Claims::getSubject);
        return !isTokenExpired(token) && username.equals(userDetails.getUsername());
    }
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    @Override
    public String generateToken(Map<String, Object> extraClaims , UserDetails userDetails, long expiration) {
        return buildToken(extraClaims, userDetails.getUsername(), expiration);
    }

    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("type", "access" );
        String subject = user.getUsername();
        return buildToken(claims, subject, accessExpiration);
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .addClaims(extraClaims)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
