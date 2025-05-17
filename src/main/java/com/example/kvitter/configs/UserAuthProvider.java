package com.example.kvitter.configs;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.UserRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {
    
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    
    @Value("${security.jwt.token.secret-key:secret-key")
    private String secretKey;
    
    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    public  String createToken(DetailedUserDto detailedUserDto){
        Date now = new Date();
        long oneHour = 60 * 60 * 1000L;
        Date validity = new Date(now.getTime() + oneHour);
        return JWT.create()
                .withIssuer(detailedUserDto.getUserName())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("email", detailedUserDto.getEmail())
                .sign(Algorithm.HMAC256(secretKey));
    }
    
    
    public Authentication validateToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        
        JWTVerifier verifier = JWT.require(algorithm).build();
        
        DecodedJWT decoded = verifier.verify(token);

        if (decoded.getExpiresAt().before(new Date())) {
            throw new ExpiredTokenException("Access token expired");
        }
        
        DetailedUserDto user = DetailedUserDto.builder()
                .userName(decoded.getIssuer())
                .email(decoded.getClaim("email").asString())
                .build();
        
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    public Authentication validateTokenStrongly(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(token);
            
            if (decoded.getExpiresAt().before(new Date())) {
                throw new ExpiredTokenException("Access token expired");
            }

            User user = userRepo.findByUserNameIgnoreCase(decoded.getIssuer())
                    .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

            return new UsernamePasswordAuthenticationToken(userMapper.userToDetailedUserDTO(user), null, Collections.emptyList());

        } catch (TokenExpiredException e) { 
            throw new ExpiredTokenException("Access token expired", e);
        } catch (JWTVerificationException e) { 
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Access Token", e);
        }
    }



}
