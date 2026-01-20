package com.example.gateway_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for WebSocket communication using the STOMP protocol.
 * <p>
 * This class sets up the real-time messaging infrastructure for the application, allowing the React frontend
 * to communicate with the Gateway without reloading the page.
 * <br>
 * <b>Configuration Details:</b>
 * <ul>
 * <li><b>Message Broker</b>: Enables an in-memory broker on the <code>/topic</code> prefix to broadcast messages back to clients.</li>
 * <li><b>Application Prefix</b>: Messages sent from the client with the <code>/app</code> prefix are routed to the <code>GameController</code>.</li>
 * <li><b>Endpoint</b>: Exposes <code>/ws-casino</code> as the connection point (Handshake URL) and enables SockJS fallback for compatibility.</li>
 * </ul>
 * </p>
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/topic");

        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws-casino")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
