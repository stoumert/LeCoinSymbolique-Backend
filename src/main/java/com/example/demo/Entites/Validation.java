package com.example.demo.Entites;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "validation")
@Data
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private Instant creation;
    private Instant expiration;
    private Instant activation;
    private String validationCode;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    private Client client;



}
