package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY="b7002f99ab5e72049948d82dc73505c65521e3fd81be87fb0f6485252349fbc7";


    public Integer extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }

    public String extrectEmail(String token){
        return extractClaim(token,Claims::getSubject);
    }


    public boolean isValid(String token, UserDetails user){
        String useremail = extrectEmail(token);
        return (useremail.equals(user.getUsername())) && !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        return extrectExpiration(token).before(new Date());
    }
    private Date extrectExpiration(String token){
        return extractClaim(token,Claims::getExpiration);

    }

    public <T> T extractClaim(String token, Function<Claims,T>resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);

    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public String generateToken(User user){
        String role = user.getRole().name();
        Integer companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        Integer departmentId = user.getDepartment() != null ? user.getDepartment().getId() : null;
        String position = user.getPosition() != null ? user.getPosition().getName() : null;
        String token = Jwts
                .builder()
                .subject(user.getEmail())
                .claim("role",role)
                .claim("userId",user.getId())
                .claim("status",user.isStatus())
                .claim("companyId",companyId)
                .claim("departmentId",departmentId)
                .claim("position",position)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+  24*60*60*1000))
                .signWith(getSigninKey())
                .compact();
        return token;
    }
    private SecretKey getSigninKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

