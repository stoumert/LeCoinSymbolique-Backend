package com.example.demo.Controller;

import com.example.demo.Entites.Annonce;
import com.example.demo.dto.AnnonceDTO;
import com.example.demo.service.AnnonceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "annonce",produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
public class AnnonceController {

    private final AnnonceService annonceService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAnnonce(@RequestPart Annonce annonce,
                              Principal principal,
                              @RequestPart(required = false, value = "images") List<MultipartFile> images
                              ){
        annonceService.createAnnonce(annonce, principal, images);
    }

    @GetMapping(path = "nom/{title}")
    public @ResponseBody List<AnnonceDTO> findByTitle(@PathVariable String title) {
        List<AnnonceDTO> annonce = annonceService.findAll(title);
        return annonce;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AnnonceDTO>> search(
            @RequestParam(required = false) String typeProduit,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String longitude,
            @RequestParam(required = false) String latitude,
            @RequestParam(required = false) String radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortedBy,
            @RequestParam(defaultValue = "desc") String sortedDir
    ) throws Exception {
        Page<AnnonceDTO> annonces = annonceService.searchAnnoncesAvecFiltresDTO(
                typeProduit,
                minPrice,
                maxPrice,
                title,
                adresse,
                longitude,
                latitude,
                radiusKm,
                page,
                size,
                sortedBy,
                sortedDir
        );

        return ResponseEntity.ok(annonces);
    }
    @DeleteMapping(path = "delete/{id}")
    public void deleteAnnonce(@PathVariable int id, Principal principal) throws IOException {
        annonceService.deleteAnnonce(id, principal);
    }

    @GetMapping(path = "get/{id}")
    public @ResponseBody AnnonceDTO getAnnonceById(@PathVariable int id){
        return annonceService.getAnnonceById(id);
    }

    @PostMapping(path = "update")
    public void updateAnnonce(@RequestBody AnnonceDTO annonce, Principal principal) throws AccessDeniedException {
        annonceService.update(annonce,principal);
    }
    @GetMapping(path = "getByUserId/{userId}")
    public Page<AnnonceDTO> getAnnonceByUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortedBy,
            @RequestParam(defaultValue = "desc") String sortedDir
    ){
        return annonceService.getAnnonceByUserId(userId,page,size,sortedBy,sortedDir);
    }

/*
    @PostMapping(path = "update/{id}")
    public @ResponseBody AnnonceDTO updateAnnonce(@RequestBody AnnonceDTO annonceDTO){
        return annonceService.updateAnnonce(annonceDTO);
    }

    @GetMapping(path = "search-title")
    public Page<Annonce> searchAnnoncesByTitle(
            @RequestParam(defaultValue = "iphone") String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortedBy,
            @RequestParam(defaultValue = "desc") String sortedDir
    ) {
        return annonceService.findByTitleContaining(title, page, size, sortedBy, sortedDir);
    }

    @GetMapping(path = "searchByType")
    public Page<Annonce> searchAnnoncesByType(@RequestParam() String typeProduit,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(defaultValue = "id") String sortedBy,
                                              @RequestParam(defaultValue = "desc") String sortedDir
    ) {
        return annonceService.getAnnoncesByTypeProduit(typeProduit,page,size,sortedBy,sortedDir);
    }
*/
}
