package com.example.demo.configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Enregistre les points de terminaison STOMP pour la communication WebSocket.
     *
     * @param registry le {@link StompEndpointRegistry} utilisé pour enregistrer les points de terminaison WebSocket
     */

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    /**
     * Configure le message broker pour la communication WebSocket.
     * Cette méthode configure le message broker avec les préfixes
     * pour les topics, les destinations d'application et les utilisateurs.
     *
     * @param config le {@link MessageBrokerRegistry} utilisé pour configurer le message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

}
