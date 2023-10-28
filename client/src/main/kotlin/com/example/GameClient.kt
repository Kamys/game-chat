package com.example

import com.github.tomaslanger.chalk.Chalk
import org.springframework.messaging.simp.stomp.*
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

data class Message(val username: String, val content: String)

fun runApp() {
    val session = StompClient.getStompSession()
    val server = GameServer(session)
    println("You successful connected to server!")
    session.subscribe("/topic/reply", object : StompFrameHandler {
        override fun getPayloadType(headers: StompHeaders): Type {
            return Message::class.java
        }

        override fun handleFrame(headers: StompHeaders, payload: Any?) {
            val message = (payload as Message)
            println("${Chalk.on(message.username).cyan()}: ${message.content}")
        }
    })


    while (true) {
        val newMessage = readln()
        if (newMessage.equals("exit", ignoreCase = true)) {
            return
        }
        server.sendMessages(newMessage)
    }
}

fun main() {
    println("Start!")
    runApp()
    println("Stop")
}

