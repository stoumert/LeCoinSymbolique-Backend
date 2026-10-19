package com.example.demo.Controller;

import com.example.demo.dto.AnnonceDTO;
import com.example.demo.service.FavorisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@Slf4j
@RestController
@RequestMapping(path = "favoris",produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@RequiredArgsConstructor
public class FavorisController {

    private final FavorisService favorisService;

    @PostMapping("/{annonceId}")
    public void addToFavoris(Principal principal, @PathVariable int annonceId) {
        favorisService.addFavori(principal, annonceId);
    }

    @DeleteMapping("/{annonceId}")
    public void removeFromFavoris(Principal principal,@PathVariable int annonceId) {
        favorisService.removeFavori(principal, annonceId);
    }

    @GetMapping("/{annonceId}")
    public boolean isFavoris(Principal principal, @PathVariable int annonceId) {
        log.info("Appel à isFavoris pour annonceId={} par {}", annonceId, principal.getName());
        return favorisService.isFavoris(principal, annonceId);
    }

    @GetMapping
    public Page<AnnonceDTO> getMyFavoris(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortedBy,
            @RequestParam(defaultValue = "desc") String sortedDir
    ) {
        return favorisService.getFavorisByClient(principal, page, size, sortedBy, sortedDir);
    }


}


