package com.example.config;

import com.example.filters.JwtAuthorizationFilter;
import com.example.filters.WebSocketsAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
//@EnableWebSocketSecurity
@EnableWebSocketMessageBroker
public class WebSocketsConfig implements WebSocketMessageBrokerConfigurer {
    String QUEUE_NAME = "ws-queue";
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    private final WebSocketsAuthenticationInterceptor webSocketsAuthenticationInterceptor;

    @Bean
    public MessageChannel myCustomChannel() {
        return new ExecutorSubscribableChannel(); // Or another suitable implementation
    }

    @Autowired
    public WebSocketsConfig(JwtAuthorizationFilter jwtAuthorizationFilter, WebSocketsAuthenticationInterceptor webSocketsAuthenticationInterceptor) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.webSocketsAuthenticationInterceptor = webSocketsAuthenticationInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("task-management-sockets").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

//    @Bean
//    public SimpMessagingTemplate messagingTemplate(MessageChannel myCustomChannel) {
//        SimpMessagingTemplate template = new SimpMessagingTemplate(myCustomChannel());
//        template.setDefaultDestination("/topic/notifications");
//        return template;
//    }

//    @Bean
//    public SimpMessagingTemplate messagingTemplate() {
//        SimpMessagingTemplate template = new SimpMessagingTemplate();
//        // Set /topic/notifications as the default destination
//        template.setDefaultDestination("/topic/notifications");
//        return template;
//    }

//    @Bean
//    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//        messages
////                .nullDestMatcher().permitAll()
////                .simpDestMatchers("/app/*").permitAll()
////                .simpSubscribeDestMatchers("/topic/**").permitAll()
////                .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.CONNECT, SimpMessageType.DISCONNECT, SimpMessageType.SUBSCRIBE).permitAll()
//                .anyMessage().permitAll();
//
//        return messages.build();
//    }

    public void configureClientInboundChannel(final ChannelRegistration registration) {
        registration.interceptors(webSocketsAuthenticationInterceptor);
    }
}
