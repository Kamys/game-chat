package com.example

import com.github.tomaslanger.chalk.Chalk
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type


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
        println("handleException $exception")
        super.handleException(session, command, headers, payload, exception)
    }
}

fun runApp() {
    val session = StompClient.getStompSession()
    val server = GameServer(session)
    println("You successful connected to server!")
    session.subscribe("/topic/reply", object : StompFrameHandler {
        override fun getPayloadType(headers: StompHeaders): Type {
            return String::class.java
        }

        override fun handleFrame(headers: StompHeaders, payload: Any?) {
            val (from, messages) = (payload as String).split(":")
            println("${Chalk.on(from).cyan()}: $messages")
        }
    })


    while (true) {
        val newMessage = readln()
        if (newMessage.equals("exit", ignoreCase = true)) {
            server.sendMessages(newMessage)
            return
        }

    }
}

fun main() {
    println("Start!")
    runApp()
    println("Stop")
}

