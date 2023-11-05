package com.example.server

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/message", "/status")
        config.setApplicationDestinationPrefixes("/app")

        /*
         config.setPathMatcher(new AntPathMatcher("."));
         config.setAuthorizeSubscriptions(true); // включить авторизацию подписок
         config.simpSubscribeDestMatchers("/user/queue/errors").permitAll()
             .simpSubscribeDestMatchers("/user/**").authenticated() // только аутентифицированные пользователи
             .simpSubscribeDestMatchers("/admin/**").hasRole("ADMIN") // только пользователи с ролью ADMIN
         */*/*/
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
    }
}