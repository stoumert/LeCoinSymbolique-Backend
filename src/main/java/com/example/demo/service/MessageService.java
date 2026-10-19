package com.example.demo.service;

import com.example.demo.Entites.Annonce;
import com.example.demo.Entites.Client;
import com.example.demo.Entites.Conversation;
import com.example.demo.Entites.Message;
import com.example.demo.dto.AnnonceDTO;
import com.example.demo.dto.ConversationDTO;
import com.example.demo.dto.ConversationPreviewDTO;
import com.example.demo.dto.MessageDTO;
import com.example.demo.mapper.AnnonceMapper;
import com.example.demo.mapper.ClientMapper;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.repository.AnnonceRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final AnnonceRepository annonceRepository;
    private final ConversationService conversationService;
    private final AnnonceMapper annonceMapper;
    private final MessageMapper messageMapper;
    private final ClientMapper clientMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    private Conversation getOrCreateConversation(Client client1, Client client2) {
        return conversationService.getOrCreateConversation(client1, client2);
    }

    public void send(Principal principal, int userid, MessageDTO messageDto, int annonceId){
        String content = messageDto.getContent();
        Client currentUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        Client client = userRepository.findById(userid).orElseThrow();

        if (annonceId == -1) {
            sendMessage(principal, userid, content);
        } else if (conversationService.findConversationByAnnonceIdAndClien1AndClien2(annonceId, currentUser, client)) {
            sendMessage(principal, userid, content);
        } else {
            sendFirstMessage(principal, userid, content, annonceId);
        }
    }



    public void sendFirstMessage(Principal principal, int userid, String content, int annonceId) {
        MessageDTO messageDTO = new MessageDTO();

        Client sender = userRepository.findByUsername(principal.getName()).orElseThrow();
        Client receiver = userRepository.findById(userid).orElseThrow(
                () -> new RuntimeException("Le client n'existe pas"));
        Optional<Annonce> annonce = annonceRepository.findById(annonceId);
        if (annonce.isEmpty()) {
            throw new RuntimeException("L'annonce n'existe pas");
        }
        Conversation conversation = getOrCreateConversation(sender, receiver);
        List<Annonce> annonces = conversation.getAnnonces();
        if (!annonces.contains(annonce.get())) {
            annonces.add(annonce.get());
            conversation.setAnnonces(annonces);
        }
        messageDTO.setSender(clientMapper.toDto(sender));
        messageDTO.setContent(content);

        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setConversation(conversation);
        messageRepository.save(message);

        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/messages",
                messageDTO
        );

    }
    public void sendMessage(Principal principal, int userid, String content) {
        MessageDTO messageDTO = new MessageDTO();

        Client sender = userRepository.findByUsername(principal.getName()).orElseThrow();
        Client receiver = userRepository.findById(userid).orElseThrow(
                () -> new RuntimeException("Le client n'existe pas"));


        Conversation conversation = getOrCreateConversation(sender, receiver);

        // Mapper vers DTO pour WebSocket
        messageDTO.setSender(clientMapper.toDto(sender));
        messageDTO.setContent(content);

        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setConversation(conversation);
        messageRepository.save(message);

        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/messages",
                messageDTO
        );
    }
    public ConversationDTO getConversationWith(Principal principal, int userId, int page, int size) {
        Client currentUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        Client otherUser = userRepository.findById(userId).orElseThrow();

        Conversation conversation = getOrCreateConversation(currentUser, otherUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());

        Page<Message> messagePage = messageRepository.findByConversationOrderBySentAtDesc(conversation, pageable);

        if (!messagePage.isEmpty()) {
            List<Message> unreadMessages = messagePage.getContent().stream()
                    .filter(message -> !message.getSender().getId().equals(currentUser.getId()))
                    .filter(message -> "unread".equalsIgnoreCase(message.getStatus()))
                    .collect(Collectors.toList());

            if (!unreadMessages.isEmpty()) {
                unreadMessages.forEach(message -> message.setStatus("lu"));
                messageRepository.saveAll(unreadMessages);
            }
        }

        Page<MessageDTO> messageDtoPage = messagePage.map(messageMapper::toDto);
        List<Annonce> annonces = conversation.getAnnonces();
        List<AnnonceDTO> annonceDTOS = annonces.stream().map(annonceMapper::toDto).toList();
        ConversationDTO dto = new ConversationDTO();
        dto.setClient1(clientMapper.toDto(conversation.getClient1()));
        dto.setClient2(clientMapper.toDto(conversation.getClient2()));
        dto.setAnnonces(annonceDTOS);
        dto.setMessages(messageDtoPage);

        return dto;
    }

    public List<ConversationPreviewDTO> getAllConversation(Principal principal) {
        List<ConversationPreviewDTO> result = new ArrayList<>();

        Client currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        List<Conversation> conversations = conversationService.findAllConversationsByUser(currentUser);

        for (Conversation conversation : conversations) {
            Client interlocuteur = conversation.getClient1().equals(currentUser)
                    ? conversation.getClient2()
                    : conversation.getClient1();

            List<Message> messages = conversation.getMessages();
            if (!messages.isEmpty()) {
                Message lastMessage = messages.getLast();
                result.add(new ConversationPreviewDTO(
                        clientMapper.toDto(interlocuteur),
                        messageMapper.toDto(lastMessage)
                ));
            }
        }

        return result;
    }

}
