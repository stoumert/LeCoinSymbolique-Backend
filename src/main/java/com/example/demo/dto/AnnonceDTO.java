package com.example.demo.dto;
import com.example.demo.Entites.Produit;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnnonceDTO{
        private Integer id;
        private String title;
        private String description;
        private BigDecimal price;
        private String status;
        private LocalDateTime createdAt;
        private ClientDTO client;
        private Produit produit;
        private String phoneNumber;
        private List<ImageDTO> images;

}
