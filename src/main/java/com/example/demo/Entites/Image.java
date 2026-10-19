package com.example.demo.Entites;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url")
    private String url;

    @Column(name = "image_type", length = 50)
    private String imageType;

    @ManyToOne
    @JoinColumn(name = "announcement_id", referencedColumnName = "id")
    private Annonce annonce;

}
