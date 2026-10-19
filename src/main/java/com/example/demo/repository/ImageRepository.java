package com.example.demo.repository;

import com.example.demo.Entites.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    List<Image> findAllByAnnonce_Id(Integer annonceId);
}
