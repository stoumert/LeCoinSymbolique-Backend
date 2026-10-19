package com.example.demo.Entites.produit;

import com.example.demo.Entites.Produit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ELECTRONIQUE")
@Data

public class Electronique extends Produit {
    private String type; // téléphone, PC, TV...
    private String marque;
    private String modele;
    private String taille;
}
