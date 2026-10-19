package com.example.demo.repository;

import com.example.demo.Entites.Client;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Client, Integer> {
    Optional<Client> findByEmail(String email);

    Optional<Client> findByUsername(String username);

    @Query("UPDATE Client c SET c.longitude = :longitude, c.latitude = :latitude WHERE c.id = :id")
    @Modifying
    void updateLongitudeAndLatitude(double longitude, double latitude, int id);

    Optional<Client> findClientByPhoneNumber(String phoneNumber);
}
