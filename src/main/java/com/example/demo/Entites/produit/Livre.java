package com.example.demo.Entites.produit;

import com.example.demo.Entites.Produit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("LIVRE")
@Data
public class Livre extends Produit {
    private String titreLivre;
    private String auteur;
    private String editeur;
    private int anneePublication;
}