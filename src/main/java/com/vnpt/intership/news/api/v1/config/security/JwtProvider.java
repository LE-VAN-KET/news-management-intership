package com.vnpt.intership.news.api.v1.config.security;

import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private static final String AUTH_APP = "auth_app_news";
    private KeyStore keyStore;
    @Value("${keystore.password}")
    private String keyStorePassword;

    @PostConstruct
    public void init() {
        try {
            // load keystore RSA
            this.keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/auth.jks");
            keyStore.load(resourceAsStream, keyStorePassword.toCharArray());
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            log.error("Exception occurred with loading keystore: {}", e.getMessage());
            throw new RuntimeException("Exception occurred while loading keystore");
        }
    }

    /**
     * Generating a JWT combine ThreadPoolTaskExecutor
     * @param auth Authentication
     * @param jwtExpirationMs Time to live of JWT
     * @return CompletableFuture<String>  contains jwt
     * */
    @Async("asyncExecutor")
    public CompletableFuture<String> generateJwtToken(Authentication auth, int jwtExpirationMs) {
        Map<String, Object> claims = new HashMap<>();
        User principal = (User) auth.getPrincipal();
        claims.put("roles", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return CompletableFuture.completedFuture(Jwts.builder().setClaims(claims).setSubject(principal.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .compact());
    }

    /**
     * Generating a JWT combine ThreadPoolTaskExecutor
     * @param user UserEntity
     * @param jwtExpirationMs Time to live of JWT
     * @return CompletableFuture<String>  contains jwt
     * */
    @Async("asyncExecutor")
    public CompletableFuture<String> generateJwtToken(UserEntity user, int jwtExpirationMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getRoleName().toString()))
                .collect(Collectors.toList()));

        return CompletableFuture.completedFuture(Jwts.builder().setClaims(claims).setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .compact());
    }

    /**
     * Retrieve PrivateKey RSA generating certificate from keytool
     * */
    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(AUTH_APP, this.keyStorePassword.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error("Occurred while retrieving public key from keystore: {}", e.getMessage());
            throw new RuntimeException("Occurred while retrieving private key from keystore");
        }
    }

    /**
     * Retrieve PublicKey RSA generating certificate from keytool
     * */
    private PublicKey getPublicKey() {
        try {
            java.security.cert.Certificate certificate = (java.security.cert.Certificate) keyStore.getCertificate(AUTH_APP);
            return certificate.getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException("Exception occurred while retrieving public key from keystore");
        }
    }

    private <T> T getClaimFromJwtToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
        return claimResolver.apply(claims);
    }

    /**
     * retrieve username from jwt
     *
     * @param token Token retrieved from client send to server
     * @return Username of user*/
    public String getUsernameFromJwtToken(String token) {
        return getClaimFromJwtToken(token, Claims::getSubject);
    }

    // check if the token has expired
    public boolean isTokenExpired(String token) {
        Date dateExpirationToken = getClaimFromJwtToken(token, Claims::getExpiration);
        return dateExpirationToken.before(new Date());
    }

    /**
     * validate token
     * @param token Token retrieved from client send to server
     * @return if compare valid then return true, else is false */
    public boolean validateJwtToken(String token) {
        try {
            // check token valid
            Jwts.parser().setSigningKey(getPrivateKey()).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT Signature: {}", e.getMessage());
            throw new TokenException("Invalid JWT Signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
            throw new TokenException("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.error("JWT Token is expired: {}", e.getMessage());
            throw new TokenException("JWT Token is expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new TokenException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
