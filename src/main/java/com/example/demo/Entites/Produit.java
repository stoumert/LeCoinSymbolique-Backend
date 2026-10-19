package com.example.demo.Entites;

import com.example.demo.Entites.produit.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Représente une entité générique de produit stockée dans une table unique au sein de la base de données.
 * Cette classe sert de classe de base pour divers types de produits, utilisant l'héritage par table unique
 * et une colonne discriminante pour différencier les types de produits.
 *
 * Une instance de cette entité contient un identifiant unique généré automatiquement (id)
 * et une colonne discriminante (`type_produit`) qui indique le type spécifique de produit.
 *
 * La classe est annotée pour une utilisation avec JPA et Jackson :
 * - Les annotations JPA gèrent la persistance de l'entité et le mappage de l'héritage.
 * - Les annotations Jackson assurent la sérialisation et la désérialisation correctes des types de sous-classes.
 *
 * Les sous-classes de cette classe représentent des catégories spécifiques de produits, comme les
 * électroniques, les vêtements, les meubles, les livres, et d'autres, chacune ayant ses
 * propres propriétés uniques.
 *
 * Cette entité utilise la stratégie d'héritage `SINGLE_TABLE`, où tous les types de produits
 * sont stockés dans une table unique, différenciés par `type_produit`.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_produit", discriminatorType = DiscriminatorType.STRING)
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type_produit",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Electronique.class, name = "ELECTRONIQUE"),
        @JsonSubTypes.Type(value = Livre.class, name = "LIVRE"),
        @JsonSubTypes.Type(value = Meuble.class, name = "MEUBLE"),
        @JsonSubTypes.Type(value = Jouet.class, name = "JOUET"),
        @JsonSubTypes.Type(value = Vetement.class, name = "VETEMENT"),
        @JsonSubTypes.Type(value = JeuVideo.class, name = "JEUVIDEO"),
        @JsonSubTypes.Type(value = Outil.class, name = "OUTIL")
})
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(insertable = false, updatable = false)
    private String type_produit;
    private String etat;
    private int annee;
}
