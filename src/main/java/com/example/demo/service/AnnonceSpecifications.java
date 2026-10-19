package com.example.demo.service;

import com.example.demo.Entites.Annonce;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AnnonceSpecifications {
    /**
     * Construit dynamiquement une Specification en fonction des filtres fournis.
     * Chaque filtre est optionnel : s'il est null ou vide, il est ignoré.
     *
     * @param typeProduit le type de produit (ex: "ELECTRONIQUE", "VETEMENT", etc.)
     * @param minPrice prix minimum à filtrer
     * @param maxPrice prix maximum à filtrer
     * @param title mot-clé à chercher dans le titre de l'annonce
     * @param userLongitude la longitude de l'utilisateur
     * @param userLatitude la latitude de l'utilisateur
     * @param radiusKm le rayon de recherche
     * @return une Specification combinant tous les filtres présents
     */
    public static Specification<Annonce> withFilters(
            String typeProduit,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String title,
            Double userLatitude,
            Double userLongitude,
            Double radiusKm
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (typeProduit != null && !typeProduit.isEmpty()) {
                predicates.add(cb.equal(root.get("produit").get("type_produit"), typeProduit.toUpperCase()));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (userLatitude != Double.MAX_VALUE && userLongitude != Double.MAX_VALUE && radiusKm != Double.MAX_VALUE) {
                Predicate distancePredicate = buildDistancePredicate(cb, root, userLatitude, userLongitude, radiusKm);
                predicates.add(distancePredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Construit un prédicat pour filtrer les objets en fonction de la distance géographique.
     * Cette méthode calcule la distance entre la position géographique d'un utilisateur
     * et les coordonnées associées à un objet, en utilisant la formule de Haversine,
     * et détermine si la distance est comprise dans un rayon donné.
     *
     * @param cb le CriteriaBuilder utilisé pour construire la requête
     * @param root l'entité racine dans la requête, représentant l'objet à filtrer
     * @param userLatitude la latitude de la position de l'utilisateur
     * @param userLongitude la longitude de la position de l'utilisateur
     * @param radiusKm le rayon dans lequel les objets doivent se trouver, en kilomètres
     * @return un Predicate qui filtre les objets selon les critères de distance spécifiés
     */

    private static Predicate buildDistancePredicate(
            CriteriaBuilder cb,
            Root<Annonce> root,
            double userLatitude,
            double userLongitude,
            double radiusKm
    ) {
        Path<Double> annonceLat = root.get("client").get("latitude");
        Path<Double> annonceLon = root.get("client").get("longitude");

        Expression<Double> userLatRad = cb.function("radians", Double.class, cb.literal(userLatitude));
        Expression<Double> userLonRad = cb.function("radians", Double.class, cb.literal(userLongitude));
        Expression<Double> annonceLatRad = cb.function("radians", Double.class, annonceLat);
        Expression<Double> annonceLonRad = cb.function("radians", Double.class, annonceLon);

        Expression<Double> cosUserLat = cb.function("cos", Double.class, userLatRad);
        Expression<Double> cosAnnonceLat = cb.function("cos", Double.class, annonceLatRad);
        Expression<Double> sinUserLat = cb.function("sin", Double.class, userLatRad);
        Expression<Double> sinAnnonceLat = cb.function("sin", Double.class, annonceLatRad);

        Expression<Double> deltaLon = cb.function("abs", Double.class, cb.diff(userLonRad, annonceLonRad));
        Expression<Double> cosDeltaLon = cb.function("cos", Double.class, deltaLon);

        Expression<Double> inner = cb.sum(
                cb.prod(cosUserLat, cb.prod(cosAnnonceLat, cosDeltaLon)),
                cb.prod(sinUserLat, sinAnnonceLat)
        );

        Expression<Double> acos = cb.function("acos", Double.class, inner);
        Expression<Double> distanceKm = cb.prod(cb.literal(6371.0), acos);

        return cb.lessThanOrEqualTo(distanceKm, radiusKm);
    }


}

