package com.example.demo.service;

import com.example.demo.Entites.*;
import com.example.demo.Entites.produit.PrixParCategorie;
import com.example.demo.dto.AnnonceDTO;
import com.example.demo.dto.ImageDTO;
import com.example.demo.exception.PriceException;
import com.example.demo.mapper.AnnonceMapper;
import com.example.demo.repository.AnnonceRepository;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.FavorisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.*;

@AllArgsConstructor
@Service
public class AnnonceService {
    /**
     * on injecte les deux dependances la premiére pour recuperer les données depuis la bd
     * la deuxieme pour transformer les annonces en annonceDto
     */
    private AnnonceRepository annonceRepository;
    private AnnonceMapper annonceMapper;
    private ConversationRepository conversationRepository;
    private UserService userService;
    private ImageService imageService;
    private FavorisRepository favorisRepository;
    /**
     * cette méthode crée les annonces et les enregistes dans la tables annonce
     * @param annonce
     */
    public void createAnnonce(Annonce annonce, Principal principal,List<MultipartFile> images){
        Client client = userService.loadUserByUsername(principal.getName());
        if (annonce.getTitle() == null){
            throw new RuntimeException("titre manquant");
        }
        if(annonce.getPrice() == null){
            throw new PriceException("prix manquant");
        }
        if(annonce.getProduit() == null){
            throw new RuntimeException("produit manquant");
        }
        if(!PrixParCategorie.isPriceValid(annonce.getPrice().doubleValue(),annonce.getProduit().getType_produit()))
            throw new PriceException("Le prix de l'annonce est trop eleve.\n Le prix de la catégorie "+
                    annonce.getProduit().getType_produit().toLowerCase()+" ne pas dépasser "
                    +PrixParCategorie.getMaxPriceByCategories(annonce.getProduit().getType_produit())+" euros.");

        annonce.getProduit().setType_produit(annonce.getProduit().getType_produit().toUpperCase());
        Produit produit = annonce.getProduit();
        annonce.setClient(client);
        annonce.setProduit(produit);
        Annonce savedAnnonce = annonceRepository.save(annonce);

        if (images != null && !images.isEmpty()) {
            List<Image> savedImages = imageService.saveImage(images, savedAnnonce);
            savedAnnonce.setImages(savedImages);

        }
    }

    public void ajouterImages(Integer idAnnonce, List<MultipartFile> files){
        Annonce annonce = annonceRepository.findAnnonceById(idAnnonce).orElseThrow();
        List<Image> images =imageService.saveImage(files, annonce);
        annonce.setImages(images);
        annonceRepository.save(annonce);
    }


    public Optional<List<Annonce>> findByTitle(String title){
        return annonceRepository.findByTitle(title);
    }

    /**
     * cette methode recupére toutes les annonces avec le meme titre puis transforme les annonces en une annoncedto
     * et fait remonter l'information à la couche supérieure
     * @param title
     * @return une liste de AnnonceDTO
     */
    public List<AnnonceDTO> findAll(String title){
        List<AnnonceDTO> annoncesDTO = new ArrayList<>();
        for (Annonce annonce : findByTitle(title).orElseThrow()){
            annoncesDTO.add(annonceMapper.toDto(annonce));
        }
        return annoncesDTO;
    }

    /**
     * Recherche des annonces avec les filtres spécifiés et options de pagination.
     *
     * @param typeProduit le type de produit pour filtrer les annonces
     * @param minPrice le prix minimum des annonces
     * @param maxPrice le prix maximum des annonces
     * @param title le titre des annonces à filtrer
     * @param adresse une adresse ou bien un arrondissement ou une ville
     * @param radiusKm un rayon de recherche
     * @param page le numéro de la page pour la pagination
     * @param size le nombre d'éléments par page
     * @param sortedBy le champ utilisé pour trier les résultats
     * @param sortedDir la direction du tri, soit "asc" pour ascendant, soit "desc" pour descendant
     * @return une liste paginée des annonces (Page<Annonce>) correspondant aux filtres spécifiés
     */
    private Page<Annonce> searchAnnoncesAvecFiltres(
            String typeProduit,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String title,
            String adresse,
            String lon,
            String lat,
            String radiusKm,
            int page,
            int size,
            String sortedBy,
            String sortedDir
    ) throws Exception {
        double latitude = Double.MAX_VALUE;
        double longitude= Double.MAX_VALUE;
        double radius= Double.MAX_VALUE;

        if (adresse != null && radiusKm != null) {
            Map<String, String> coordinates = LocalisationService.getCoordinateWithLocalisation(adresse);
            longitude = Double.parseDouble(coordinates.get("lon"));
            latitude = Double.parseDouble(coordinates.get("lat"));
            radius = Double.parseDouble(radiusKm);
        }if (lon != null && lat != null) {
            longitude = Double.parseDouble(lon);
            latitude = Double.parseDouble(lat);
            if (radiusKm != null) {
                radius = Double.parseDouble(radiusKm);
            }
        }

        Sort sort = sortedDir.equalsIgnoreCase("desc") ? Sort.by(sortedBy).descending() : Sort.by(sortedBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Annonce> spec = AnnonceSpecifications.withFilters(typeProduit,
                minPrice,
                maxPrice,
                title,
                latitude,
                longitude,
                radius
        );
        return annonceRepository.findAll(spec, pageable);
    }

    /**
     * Recherche des annonces en fonction des filtres fournis et retourne une liste paginée d'objets AnnonceDTO.
     *
     * @param typeProduit le type de produit pour filtrer les annonces
     * @param minPrice le prix minimum pour filtrer les annonces
     * @param maxPrice le prix maximum pour filtrer les annonces
     * @param title le titre (ou mot-clé) pour filtrer les annonces
     * @param page le numéro de la page pour la pagination
     * @param size le nombre d'éléments par page pour la pagination
     * @param sortedBy le champ utilisé pour trier les résultats
     * @param sortedDir la direction du tri (par exemple : ascendant "asc" ou descendant "desc")
     * @return une liste paginée d'objets AnnonceDTO correspondant aux filtres donnés
     */
    public Page<AnnonceDTO> searchAnnoncesAvecFiltresDTO(
            String typeProduit,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String title,
            String adresse,
            String longitude,
            String latitude,
            String radiusKm,
            int page,
            int size,
            String sortedBy,
            String sortedDir
    ) throws Exception {
        Page<Annonce> annonces = searchAnnoncesAvecFiltres(typeProduit, minPrice, maxPrice, title, adresse,longitude, latitude, radiusKm, page, size, sortedBy, sortedDir);


        return annonces.map(annonceMapper::toDto);
    }
    @Transactional
    public void deleteAnnonce(int id, Principal principal) throws IOException {
        Client client = userService.loadUserByUsername(principal.getName());

        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce non trouvée"));

        if (!Objects.equals(client.getId(), annonce.getClient().getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer cette annonce.");
        }

        favorisRepository.deleteByAnnonce(annonce);
        for (Image image : annonce.getImages()){
            imageService.deleteImage(image);
        }
        List<Conversation> conversations = conversationRepository.findByAnnoncesContaining(annonce);
        for (Conversation conversation : conversations) {
            conversation.getAnnonces().remove(annonce);
            conversationRepository.save(conversation);
        }

        annonceRepository.delete(annonce);
    }

    public AnnonceDTO getAnnonceById(int id){
        List<ImageDTO> images = new ArrayList<>();

        return annonceMapper.toDto(annonceRepository.findById(id).orElseThrow());
    }

    public void update(AnnonceDTO annonceDTO, Principal principal) throws AccessDeniedException {
        Client client = userService.loadUserByUsername(principal.getName());
        Annonce annonce = annonceRepository.findAnnonceById(annonceDTO.getId())
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée avec l'ID: " + annonceDTO.getId()));
        if (!client.getId().equals(annonce.getClient().getId())){
            throw new AccessDeniedException("Vous êtes pas autorisé a modifier cette annonce.");
        }

        if (annonceDTO.getTitle() != null) {
            annonce.setTitle(annonceDTO.getTitle());
        }
        if (annonceDTO.getPrice() != null) {
            if(!PrixParCategorie.isPriceValid(annonce.getPrice().doubleValue(),annonce.getProduit().getType_produit()))
                throw new PriceException("Le prix de l'annonce est trop eleve.\n Le prix de la catégorie "+
                        annonce.getProduit().getType_produit().toLowerCase()+" ne pas dépasser "
                        +PrixParCategorie.getMaxPriceByCategories(annonce.getProduit().getType_produit())+" euros.");
            annonce.setPrice(annonceDTO.getPrice());
        }
        if (annonceDTO.getDescription() != null) {
            annonce.setDescription(annonceDTO.getDescription());
        }
        annonceRepository.save(annonce);
    }

    public Page<AnnonceDTO> getAnnonceByUserId(int userId, int page, int size, String sortedBy, String sortedDir) {
        Sort sort = sortedDir.equalsIgnoreCase("desc") ? Sort.by(sortedBy).descending() : Sort.by(sortedBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Annonce> annonces = annonceRepository.findAnnoncesByClientId(userId, pageable);
        return annonces.map(annonceMapper::toDto);
    }

/*
    public List<AnnonceDTO> findBySearch(Specification<Annonce> specs) {
        List<Annonce> annonces = annonceRepository.findAll(Specification.where(specs));
        if (annonces.isEmpty()) {
            throw new RuntimeException("Aucune annonce trouvée pour les critères donnés.");
        }
        return annonces.stream()
                .map(annonceMapper::toDto)
                .collect(Collectors.toList());
    }
    public Page<Annonce> getAllAnnonces(
            int page,
            int size,
            String sortedBy,
            String sortedDir
    ){
        var sort = sortedDir.equalsIgnoreCase("desc") ?
                Sort.by(sortedBy).descending()
                : Sort.by(sortedBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return annonceRepository.findAll(pageable);
    }
    public Page<Annonce> getAnnoncesByProduit_typeAndPriceBetween(
            String produit,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortedBy,
            String sortedDir
    ){
        var sort = sortedDir.equalsIgnoreCase("desc") ?
                Sort.by(sortedBy).descending()
                : Sort.by(sortedBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        if (produit != null){
            produit = produit.toUpperCase();
        }

        return annonceRepository.findByTypeProduitAndPriceBetween(
                produit,
                minPrice,
                maxPrice,
                pageable);
    }

    public Page<Annonce> findByTitleContaining(String title,
                                               int page,
                                               int size,
                                               String sortedBy,
                                               String sortedDir){
        var sort = sortedDir.equalsIgnoreCase("desc") ?
                Sort.by(sortedBy).descending()
                : Sort.by(sortedBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return annonceRepository.findByTitleContaining(title, pageable);
    }
    public Page<Annonce> getAnnoncesByTypeProduit(String typeProduit,
                                                  int page,
                                                  int size,
                                                  String sortedBy,
                                                  String sortedDir) {
        var sort = sortedDir.equalsIgnoreCase("desc") ?
                Sort.by(sortedBy).descending()
                : Sort.by(sortedBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return annonceRepository.findByTypeProduit(typeProduit,pageable);
    }

 */
}