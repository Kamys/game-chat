package com.example

import com.github.tomaslanger.chalk.Chalk
import jakarta.websocket.ClientEndpointConfig
import okhttp3.internal.wait
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.net.ConnectException


class MyStompSessionHandler : StompSessionHandlerAdapter() {
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable,
    ) {
        println("handleException ${exception}")
        super.handleException(session, command, headers, payload, exception)
    }
}

object Authorisation {
    private var httpClient = GameHttpClient()
    private var token = null

    fun getToken(): String {
        if (token != null) {
            return token as Nothing
        }

        println("Please, select option:")
        val option = selectOption(
            1 to "Login",
            2 to "Create new account"
        )

        while (true) {
            val username = inputText("User name")
            val password = inputText("Password")

            return if (option == 1) {
                httpClient.login(username, password)
            } else {
                httpClient.register(username, password)
            }
        }
    }
}

fun runApp() {
    val token = Authorisation.getToken()
    val url = "ws://localhost:8080/ws"
    val webSocketClient = StandardWebSocketClient()
    val stompClient = WebSocketStompClient(webSocketClient)
    stompClient.messageConverter = StringMessageConverter()
    val sessionHandler: StompSessionHandler = MyStompSessionHandler()

    val headers = WebSocketHttpHeaders()
    headers.add("Authorization", "Bearer $token")

    val session = stompClient.connectAsync(url, headers, sessionHandler).join()
    println("You successful connected to server!")
    session.subscribe("/topic/reply", object : StompFrameHandler {
        override fun getPayloadType(headers: StompHeaders): Type {
            return String::class.java
        }

        override fun handleFrame(headers: StompHeaders, payload: Any) {
            val (from, messages) = (payload as String).split(":")
            println("${Chalk.on(from).cyan()}: $messages")
        }
    })


    while (true) {
        val newMessage = readln()
        if (newMessage.equals("exit", ignoreCase = true)) {
            return
        }
        session.send("/app/hello/reply", newMessage)
    }
}

fun main() {
    println("Start!")
    runApp()
    println("Stop")
}

