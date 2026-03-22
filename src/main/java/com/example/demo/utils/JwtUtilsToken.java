package com.example.demo.utils;

import com.example.demo.entity.UsersEntity;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Component
public class JwtUtilsToken {
    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(UsersEntity usersEntity,boolean is2faPassed) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",usersEntity.getRolename());
        claims.put("2faPassed",is2faPassed);
        long expMillis = Long.parseLong(expiration)*1000L;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usersEntity.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private  Claims getClaimsFromToken(String token) {
        return  Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public  String extractUsernameFromToken(String token) {
        return getClaimFromToken(token,Claims::getSubject);
    }
    public boolean extract2faStatus(String token) {
        Boolean status = getClaimFromToken(token, claims -> claims.get("2fa_passed", Boolean.class));
        return status != null && status;
    }
    public String extractRole(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }


    public int getRemainingExpiration(String token) {
        Date exp = this.getClaimsFromToken(token).getExpiration();
        long remainingMillis = exp.getTime() - System.currentTimeMillis();
        return (int) Math.max(remainingMillis / 1000, 0);
    }

    public boolean isTokenExpired(String token) {
        final Date exp = this.getClaimsFromToken(token).getExpiration();
        return exp.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
