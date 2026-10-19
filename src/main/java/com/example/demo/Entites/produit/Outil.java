package com.example.demo.Entites.produit;

import com.example.demo.Entites.Produit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("OUTIL")
@Data
public class Outil extends Produit {
    private String type; // perceuse, marteau...
    private String puissance;
    private String marque;
    private String modele;
}