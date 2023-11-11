package com.example.server

import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.WebSocketHttpHeaders
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomString(from: Int = 6, to: Int = 20): String {
    require(from <= to) { "Invalid range: from=$from, to=$to" }
    val length = Random.nextInt(from, to + 1)
    return buildString(length) {
        repeat(length) {
            append(charPool.random())
        }
    }
}

class TestConnectionHandler : StompSessionHandlerAdapter() {
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