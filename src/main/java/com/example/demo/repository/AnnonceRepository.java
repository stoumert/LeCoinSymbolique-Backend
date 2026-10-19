package com.example.demo.repository;

import com.example.demo.Entites.Annonce;
import com.example.demo.Entites.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface du repository pour gérer les entités Annonce.
 * Cette interface fournit des fonctionnalités CRUD de base à l'aide de JpaRepository
 * et ajoute des méthodes de recherche avancée via JpaSpecificationExecutor.
 */
public interface AnnonceRepository extends JpaRepository<Annonce, Integer>, JpaSpecificationExecutor<Annonce> {

    /**
     * Recherche d'annonces par titre.
     * @param title Le titre de l'annonce.
     * @return une liste d'annonces ou une exception.
     */
    Optional<List<Annonce>> findByTitle(String title);

    Optional<List<Annonce>> findByProduit(Produit produit);

    @Query("SELECT a FROM Annonce a WHERE a.produit.type_produit = :typeProduit AND a.price BETWEEN :minPrice AND :maxPrice")
    Page<Annonce> findByTypeProduitAndPriceBetween(
            @Param("typeProduit") String typeProduit,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT a FROM Annonce a WHERE a.title LIKE %:title%")
    Page<Annonce> findByTitleContaining(String title, Pageable pageable);
    @Query("SELECT a FROM Annonce a WHERE a.produit.type_produit = :typeProduit")
    Page<Annonce> findByTypeProduit(
            @Param("typeProduit") String typeProduit,
            Pageable pageable
    );

    Optional<Annonce> findAnnonceById(int annonceId);

    @Modifying
    @Query("DELETE FROM Annonce a WHERE a.id = :annonceId")
    void deleteAnnonceById(int annonceId);

    Page<Annonce> findAnnoncesByClientId(Integer clientId,
                                         Pageable pageable);
}
