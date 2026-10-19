package com.example.demo.service;

import com.example.demo.Entites.*;
import com.example.demo.dto.AnnonceDTO;
import com.example.demo.mapper.AnnonceMapper;
import com.example.demo.repository.AnnonceRepository;
import com.example.demo.repository.FavorisRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des favoris des utilisateurs. Fournit des fonctionnalités pour ajouter, supprimer,
 * récupérer et vérifier si un élément est marqué comme favori pour un utilisateur spécifique.
 */
@Slf4j
@Service
@AllArgsConstructor
public class FavorisService {

    private FavorisRepository favorisRepository;
    private AnnonceRepository annonceRepository;
    private AnnonceMapper annonceMapper;
    private UserService userService;

    /**
     * Ajoute une annonce à la liste des favoris du client si elle n'est pas déjà présente.
     *
     * @param principal Le principal de sécurité représentant l'utilisateur actuellement authentifié.
     * @param annonceId L'identifiant de l'annonce à ajouter aux favoris.
     */

    public void addFavori(Principal principal, int annonceId) {
        Annonce annonce = annonceRepository.findAnnonceById(annonceId).orElseThrow();
        Client client = userService.loadUserByUsername(principal.getName());

        Optional<Favoris> existing = favorisRepository.findByClientAndAnnonceId(client, annonceId);
        if (existing.isEmpty()) {
            Favoris favoris = new Favoris();
            favoris.setClient(client);
            favoris.setAnnonce(annonce);
            favorisRepository.save(favoris);
        }
    }

    /**
     * Supprime une annonce de la liste des favoris du client spécifié.
     * Cette méthode est transactionnelle pour garantir l'intégrité des données lors de la modification
     * de la relation entre le client et ses favoris dans la base de données.
     *
     * @param principal Le principal de sécurité représentant l'utilisateur actuellement authentifié.
     * @param annonceId L'identifiant de l'annonce à supprimer des favoris.
     */
    @Transactional
    public void removeFavori(Principal principal, int annonceId) {
        Client client = userService.loadUserByUsername(principal.getName());
        favorisRepository.deleteByClientAndAnnonceId(client, annonceId);
    }

    /**
     * Récupère la liste paginée des annonces favorites de l'utilisateur authentifié.
     *
     * @param principal Le principal de sécurité représentant l'utilisateur actuellement authentifié.
     * @param page Le numéro de la page demandée (commence à 0).
     * @param size Le nombre d'éléments par page.
     * @param sortedBy Le critère de tri des annonces (ex: "date", "prix", etc.).
     * @param sortedDir La direction du tri ("asc" pour ascendant ou "desc" pour descendant).
     * @return Une page contenant les annonces favorites de l'utilisateur selon les critères spécifiés.
     */
    public Page<AnnonceDTO> getFavorisByClient(Principal principal,
                                               int page,
                                               int size,
                                               String sortedBy,
                                               String sortedDir) {

        Sort sort = sortedDir.equalsIgnoreCase("desc") ?
                Sort.by(sortedBy).descending() :
                Sort.by(sortedBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Client client = userService.loadUserByUsername(principal.getName());

        List<Favoris> favorisList = favorisRepository.findByClient(client);
        List<AnnonceDTO> annonceDTOList = favorisList.stream()
                .map(favoris -> annonceMapper.toDto(favoris.getAnnonce()))
                .toList();

        int start = Math.min((int) pageable.getOffset(), annonceDTOList.size());
        int end = Math.min(start + pageable.getPageSize(), annonceDTOList.size());
        List<AnnonceDTO> pagedList = annonceDTOList.subList(start, end);

        return new PageImpl<>(pagedList, pageable, annonceDTOList.size());
    }

    /**
     * Vérifie si une annonce spécifique est dans les favoris de l'utilisateur authentifié.
     *
     * @param principal Le principal de sécurité représentant l'utilisateur actuellement authentifié.
     * @param annonceId L'identifiant de l'annonce à vérifier.
     * @return true si l'annonce est dans les favoris de l'utilisateur, false sinon.
     */
    public boolean isFavoris(Principal principal, int annonceId) {
        Client client = userService.loadUserByUsername(principal.getName());
        return favorisRepository.findByClientAndAnnonceId(client, annonceId).isPresent();

    }

}
