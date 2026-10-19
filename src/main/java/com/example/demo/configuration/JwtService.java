package com.example.demo.configuration;

import com.example.demo.Entites.Client;
import com.example.demo.Entites.Jwt;
import com.example.demo.exception.NotFoundJwt;
import com.example.demo.repository.JwtRepository;
import com.example.demo.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Transactional //Nécessaire si on effectue des opérations en base de données et qu'on veut assurer l'intégrité des données.
@AllArgsConstructor
@Service
public class JwtService {
    private final String ENCRYPTION_KEY = "c0ba6d5232434bd54e4645134fe36f0cc67d4040ff22accba4e6c02e4d21614f";
    private UserService userService;
    private JwtRepository jwtRepository;
    public Map<String,String> generateToken(String username) {
        Client client = this.userService.loadUserByUsername(username);
        Map<String, String> generatedJWT = this.generateJWT(client);
        return generatedJWT;
    }
    public Jwt findJwtByToken(String token) {
        return this.jwtRepository.findByToken(token);
    }

    /**
     * dans cette methode on génere un token a un client apres qu'il se soit connecté et on stock le token dans la bd
     * @param client
     * @return map<Bearer, Token>
     */
    public Map<String, String> generateJWT(Client client) {
        disableToken(client); //si le client a deja un token, on le désactive avant de lui attribue un nouveau

        String refreshToken = buildRefreshToken(client);
        String accessToken = buildAccessToken(client.getUsername());
        Jwt jwtEntity = new Jwt();
        jwtEntity.setToken(accessToken);
        jwtEntity.setClient(client);
        jwtEntity.setRefreshToken(refreshToken);
        jwtRepository.save(jwtEntity);

        return Map.of(
                "bearer", accessToken,
                "refreshToken", refreshToken
                );
    }
    public String buildAccessToken(String username) {
        Client client = this.userService.loadUserByUsername(username);
        Jwt jwt = jwtRepository.getJwtByClient(client);
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000;
        if (jwt == null) {
            return tokenBuilder(client,expirationTime,currentTime);
        }else {
            jwt.setToken(tokenBuilder(client,expirationTime,currentTime));
        }
        return jwt.getToken();
    }
    private String buildRefreshToken(Client client) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 10080 * 60 * 1000 ; // 7 jours
        return tokenBuilder(client, expirationTime,currentTime);
    }
    private String tokenBuilder(Client client, long expirationTime, long currentTime) {
        final Map<String, Object> claims = Map.of(
                "username", client.getUsername(),
                "email", client.getEmail(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, client.getUsername()
        );
        return Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(client.getEmail())
                .addClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public String extractUsernamme(String token) {
        return this.getClaims(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = this.getClaims(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    private <T> T getClaims(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);

    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * methode qui nous permet de mettre de désactivé un token dans la bd
     * @param client
     */
    private void disableToken(Client client) {
        List<Jwt> jwts = jwtRepository.findByClient(client.getId()).peek(
                jwt -> {
                    jwt.setExpired(true);
                }
        ).toList();
        jwtRepository.saveAll(jwts);
    }

    /**
     * cette methode nous permet de mettre de déconnecter un utilisateur en désactivant son token
     * @param principal
     */
    public void deconnect(Principal principal) {
        Client client = this.userService.loadUserByUsername(principal.getName());
        disableToken(client);
    }

    /**
     * Methode qui nettoie la base de donnée des tokens éxpiré
     */
    @Scheduled(cron = "0 * * * * *")
    protected void supprimerAllToken(){
        jwtRepository.deleteByExpired(true);
    }

    public boolean isRefreshToken(String refreshToken) throws NotFoundJwt {
        if (jwtRepository.existsJwtByRefreshToken(refreshToken))
            return true;
        else throw new NotFoundJwt("Refresh token not found");
    }
}
