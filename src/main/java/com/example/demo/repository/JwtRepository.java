package com.example.demo.repository;

import com.example.demo.Entites.Client;
import com.example.demo.Entites.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.stream.Stream;


public interface JwtRepository extends CrudRepository<Jwt, Integer> {
    Jwt findByToken(String jwt);

    @Query("SELECT j FROM Jwt j WHERE j.client.id = :id")
    Stream<Jwt> findByClient(Integer id);

    void deleteByExpired(boolean expired);

    String getJwyByClient(Client client);

    Optional<String> getTokenByClient(Client client);

    Jwt getJwtByClient(Client client);

    boolean existsJwtByRefreshToken(String refreshToken);

}
