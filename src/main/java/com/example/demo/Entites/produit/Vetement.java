package com.example.demo.Entites.produit;

import com.example.demo.Entites.Produit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("VETEMENT")
@Data
public class Vetement extends Produit {
    private String taille;
    private String genre; // homme, femme, unisexe
    private String type;  // t-shirt, pantalon, veste...
    private String marque;
    private String couleur;
}

