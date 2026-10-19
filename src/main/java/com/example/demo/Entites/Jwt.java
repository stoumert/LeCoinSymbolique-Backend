package com.example.demo.Entites;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Jwt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String refreshToken;
    private boolean expired;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;
}
