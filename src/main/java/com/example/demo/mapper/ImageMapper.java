package com.example.demo.mapper;

import com.example.demo.Entites.Image;
import com.example.demo.dto.ImageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ImageMapper {
    @Mapping(source = "image.url", target = "url")
    ImageDTO toDto(Image image);
    Image toEntity(ImageDTO imageDTO);
}
