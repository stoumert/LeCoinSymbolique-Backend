package com.example.demo.Entites.produit;

import com.example.demo.Entites.Produit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("JEU_VIDEO")
@Data
public class JeuVideo extends Produit {
    private String titreJeu;
    private String plateforme; // PS5, Xbox, PC...
    private String editeur;
}