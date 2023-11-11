package com.example.server

import com.example.server.auth.TokenService
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.concurrent.TimeUnit

class WebSocketTest: BaseTest() {
    private val webSocketStompClient = createWebSocketStompClient()

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tokenService: TokenService

    fun createWebSocketConnection(): Pair<StompSession, User> {
        val user = userRepository.save(User(username = "Alex", password = randomString()))
        val token = tokenService.createToken(user)
        val httpHeaders = WebSocketHttpHeaders().apply {
            add("Authorization", "Bearer $token")
        }
        val stompSession = webSocketStompClient
            .connectAsync("ws://localhost:$serverPort/ws", httpHeaders, TestConnectionHandler())
            .get(1, TimeUnit.SECONDS)

        return Pair(stompSession, user)
    }

    companion object {
        fun createWebSocketStompClient(): WebSocketStompClient {
            val client = WebSocketStompClient(StandardWebSocketClient())
            client.messageConverter = MappingJackson2MessageConverter().apply {
                objectMapper.registerModule(KotlinModule.Builder().build())
            }
            return client
        }
    }
}