package com.playconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Sets up the STOMP-over-WebSocket connection point. Day 53's chat
 * feature publishes/subscribes on top of this — nothing chat-specific
 * lives here, just the shared plumbing.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Clients connect here first to establish the WebSocket connection.
        // SockJS is a fallback for browsers/networks that block raw
        // WebSocket — it transparently uses HTTP long-polling instead
        // when needed, so the frontend code doesn't have to know or care.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173") // same origin as Day 47's CORS setup
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages a client sends go to destinations prefixed "/app" —
        // routed to an @MessageMapping method in a controller (Day 53).
        registry.setApplicationDestinationPrefixes("/app");

        // "/topic" is for broadcast messages (many subscribers, e.g. all
        // players in a match's chat). Spring's built-in simple broker
        // handles routing messages to everyone subscribed to a given
        // topic — no external message broker (like RabbitMQ) needed for
        // this app's scale.
        registry.enableSimpleBroker("/topic");
    }
}