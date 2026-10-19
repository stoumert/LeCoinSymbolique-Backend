package com.example.demo.mapper;
import com.example.demo.Entites.Client;
import com.example.demo.dto.ClientDTO;
import org.mapstruct.Mapper;

/**
 * Cette interface nous permet de mapper le mapper les clients en dto et vis versa.
 * Éviter de faire remonter des informations sensibles comme les mots de passes
 * On utlise mapstruct 
 */
@Mapper(componentModel = "spring")
public interface ClientMapper {

    // Mapper Client -> ClientDto
    ClientDTO toDto(Client client);

    // Mapper ClientDto -> Client (si nécessaire, pour les mises à jour ou créations)
    Client toEntity(ClientDTO clientDto);
}