package com.example.demo.repository;

import com.example.demo.Entites.Validation;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

public interface ValidationRepository extends CrudRepository<Validation, Integer> {
    Optional<Validation> findByValidationCode(String validationCode);
    void deleteByExpirationBefore(Instant now);
}
