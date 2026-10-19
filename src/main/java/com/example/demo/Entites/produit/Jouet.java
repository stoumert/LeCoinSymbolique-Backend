package com.example.demo.Entites.produit;

import com.example.demo.Entites.Produit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("JOUET")
@Data
public class Jouet extends Produit {
    private String type; // peluche, jeu de société, figurine...
    private int ageMinimum;
    private String marque;
}