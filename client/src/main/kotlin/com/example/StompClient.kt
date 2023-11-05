package com.example

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient

class MyStompSessionHandler : StompSessionHandlerAdapter() {
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        println("afterConnected")
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable,
    ) {
        println("handleException $exception")
        super.handleException(session, command, headers, payload, exception)
    }
}

object StompClient {
    fun getStompSession(token: String): StompSession {
        val url = "ws://localhost:8080/ws"
        val webSocketClient = StandardWebSocketClient()
        val stompClient = WebSocketStompClient(webSocketClient)
        stompClient.messageConverter = createMessageConverter()
        val sessionHandler = MyStompSessionHandler()

        val headers = WebSocketHttpHeaders()
        headers.add("Authorization", "Bearer $token")


        return stompClient.connectAsync(url, headers, sessionHandler).join()
    }

    private fun createMessageConverter(): MappingJackson2MessageConverter {
        return MappingJackson2MessageConverter().apply {
            objectMapper.registerModule(KotlinModule.Builder().build())
        }
    }
}