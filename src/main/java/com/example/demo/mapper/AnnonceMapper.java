package com.example.demo.mapper;

import com.example.demo.Entites.Annonce;
import com.example.demo.dto.AnnonceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")

public interface AnnonceMapper {

    @Mapping(source="client.phoneNumber", target = "phoneNumber")
    AnnonceDTO toDto(Annonce annonce);
    Annonce toEntity(AnnonceDTO annonceDTO);
}
