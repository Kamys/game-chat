package com.example

import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient

object StompClient {
    fun getStompSession(): StompSession {
        val token = Authorisation.getToken()
        val url = "ws://localhost:8080/ws"
        val webSocketClient = StandardWebSocketClient()
        val stompClient = WebSocketStompClient(webSocketClient)
        stompClient.messageConverter = StringMessageConverter()
        val sessionHandler: StompSessionHandler = MyStompSessionHandler()

        val headers = WebSocketHttpHeaders()
        headers.add("Authorization", "Bearer $token")

        return stompClient.connectAsync(url, headers, sessionHandler).join()
    }
}