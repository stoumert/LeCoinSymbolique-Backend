package com.example.demo.repository;
import com.example.demo.Entites.Conversation;
import com.example.demo.Entites.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MessageRepository extends JpaRepository<Message, Integer> {

    Page<Message> findByConversationOrderBySentAtDesc(Conversation conversation, Pageable pageable);




}
