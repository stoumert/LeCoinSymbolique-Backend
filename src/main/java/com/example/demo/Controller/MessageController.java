package com.example.demo.Controller;
import com.example.demo.dto.ConversationDTO;
import com.example.demo.dto.ConversationPreviewDTO;
import com.example.demo.dto.MessageDTO;
import com.example.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "messages",produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping(path = "send/{userid}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendMessage(
            Principal principal,
            @PathVariable int userid,
            @RequestBody MessageDTO messageDto,
            @RequestParam(defaultValue = "-1") int annonceId
    ) {
        messageService.send(principal,userid,messageDto,annonceId);
    }

    @GetMapping(path = "conversation/all")
    public @ResponseBody List<ConversationPreviewDTO> getAllConversations(Principal principal) {
        return messageService.getAllConversation(principal);
    }
    @GetMapping(path = "conversation/{userid}")
    public ConversationDTO getConversation(
            Principal principal,
            @PathVariable int userid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return messageService.getConversationWith(principal, userid, page, size);
    }
    /*
    @GetMapping(path = "messagerie")
    public Page<Message> getMessagerie(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return messageService.findBySender(page,size);
    }

     */
}
