package com.example.demo.Entites.produit;

import com.example.demo.Entites.Produit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("MEUBLE")
@Data
public class Meuble extends Produit {
    private String type;  // table, lit, canapé...
    private String matiere;
    private String dimensions;
    private String couleur;
}