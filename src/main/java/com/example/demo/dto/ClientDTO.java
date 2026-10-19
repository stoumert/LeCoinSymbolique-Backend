package com.example.demo.dto;

import com.example.demo.Entites.Role;
import lombok.Data;

/**
    cette classe nous permet de controller les informations que l'on veut faire remonter, par exemple
    on a pas envie de faire remonter les mots de passes des clients
    DTO -> Data Transfert Object 
* */

@Data
public class ClientDTO {
    private Integer id;
    private String email;
    private String username;
    private String fullName;
    private String location;
    private Role role;
}
