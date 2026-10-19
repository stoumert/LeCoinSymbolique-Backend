package com.example.demo.service;

import com.example.demo.Entites.Client;
import com.example.demo.Entites.Conversation;
import com.example.demo.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public Conversation getOrCreateConversation(Client client1, Client client2) {


        Optional<Conversation> existing = conversationRepository.findBetweenClients(client1, client2);
        Client finalClient = client1;
        Client finalClient1 = client2;
        return existing.orElseGet(() -> {
            Conversation newConv = new Conversation();
            newConv.setClient1(finalClient);
            newConv.setClient2(finalClient1);
            return conversationRepository.save(newConv);
        });
    }

    public List<Conversation> findAllConversationsByUser(Client user) {
        return conversationRepository.findConversationByClient1OrClient2(user);
    }
    public boolean findConversationByAnnonceIdAndClien1AndClien2(int annonceID, Client client1, Client client2){
        return conversationRepository.findConversationByAnnoncesAndClient1AndClient2(annonceID,client1,client2).isPresent();
    }
}
