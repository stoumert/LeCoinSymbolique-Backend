package com.example.demo.repository;

import com.example.demo.Entites.Annonce;
import com.example.demo.Entites.Client;
import com.example.demo.Entites.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.client1 = :client1 AND c.client2 = :client2) OR " +
            "(c.client1 = :client2 AND c.client2 = :client1)")
    Optional<Conversation> findBetweenClients(Client client1, Client client2);

    @Query("SELECT c FROM Conversation c WHERE c.client1 = :user OR c.client2 = :user")
    List<Conversation> findConversationByClient1OrClient2(Client user);

    @Query("""
    SELECT c FROM Conversation c
    JOIN c.annonces a
    WHERE a.id = :annonceId
    AND (
        (c.client1 = :client1 AND c.client2 = :client2)
        OR
        (c.client1 = :client2 AND c.client2 = :client1)
        )
    """)
    Optional<Conversation> findConversationByAnnoncesAndClient1AndClient2(
            @Param("annonceId") int annonceId,
            @Param("client1") Client client1,
            @Param("client2") Client client2
    );

    List<Conversation> findByAnnoncesContaining(Annonce annonce);

}
