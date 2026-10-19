package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversationPreviewDTO {
    private ClientDTO interlocuteur;
    private MessageDTO lastMessage;

}
