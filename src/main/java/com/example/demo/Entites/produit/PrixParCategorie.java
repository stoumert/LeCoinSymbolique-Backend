package com.example.demo.Entites.produit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor
public class PrixParCategorie {

    public static double getMaxPriceByCategories(String categorie){
        return switch (categorie.toLowerCase()) {
            case "electronique" -> 50.0;
            case "meuble" -> 30.0;
            case "jouet" -> 10.0;
            case "livre" -> 5.0;
            case "outil" -> 20.0;
            case "vetement" -> 10.0;
            case "jeuvideo" -> 5.0;
            default -> 1.0;
        };
    }
    public static boolean isPriceValid(double price, String categories){
        return price <= getMaxPriceByCategories(categories);
    }
}
