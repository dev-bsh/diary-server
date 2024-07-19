package com.diary_server.util;

import com.diary_server.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.token.period}")
    private long tokenPeriod;
    @Value("${jwt.refresh-token.period}")
    private long refreshTokenPeriod;

    private Key key;

    private final String ACCESS = "access";
    private final String REFRESH = "refresh";

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(Long userId, Role role) {
        return createToken(userId, role, tokenPeriod, ACCESS);
    }

    public String createRefreshToken(Long userId, Role role) {
        return createToken(userId, role, refreshTokenPeriod, REFRESH);
    }

    public String createToken(Long userId, Role role, long period, String type) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("role", role);
        claims.put("type", type);

        Date now = new Date();
        Date validity = new Date(now.getTime() + period);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccessToken(String token) {
        String type = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("type", String.class);
        return type.equals(ACCESS);
    }

    public boolean isRefreshToken(String token) {
        String type = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("type", String.class);
        return type.equals(REFRESH);
    }

    public Long getUserId(String token) {
        return Long.valueOf(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject());
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            // 토큰 유효성 및 만료기간 검사
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token is expired : {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token is invalid : {}", e.getMessage());
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Date getExpiryDateFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
    }
}
