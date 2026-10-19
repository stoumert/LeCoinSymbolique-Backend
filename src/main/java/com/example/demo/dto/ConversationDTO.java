package com.example.demo.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ConversationDTO {
    private ClientDTO client1;
    private ClientDTO client2;
    private List<AnnonceDTO> annonces;
    private Page<MessageDTO> messages;
}
