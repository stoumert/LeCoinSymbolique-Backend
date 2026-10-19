package com.example.demo.repository;

import com.example.demo.Entites.Annonce;
import com.example.demo.Entites.Client;
import com.example.demo.Entites.Favoris;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FavorisRepository extends JpaRepository<Favoris, Integer> {
    void deleteFavorisById(Integer id);
    List<Favoris> findByClient(Client client);

    Optional<Favoris> findByClientAndAnnonceId(Client client, int annonceId);

    void deleteByClientAndAnnonceId(Client client, int annonceId);

    void deleteByAnnonce(Annonce annonce);

}
