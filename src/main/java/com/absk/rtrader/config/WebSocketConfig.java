package com.absk.rtrader.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.absk.rtrader.core.constants.CoreConstants;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins(CoreConstants.FRONTEND_BASE_URI).withSockJS();
        registry.addEndpoint("/historical").setAllowedOrigins(CoreConstants.FRONTEND_BASE_URI);
        registry.addEndpoint("/get-auth-data").setAllowedOrigins(CoreConstants.FRONTEND_BASE_URI);
        
    }

}
