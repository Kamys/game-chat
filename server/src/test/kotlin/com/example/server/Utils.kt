package com.example.server

import org.springframework.messaging.simp.stomp.*
import org.springframework.web.socket.WebSocketHttpHeaders
import java.lang.reflect.Type
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
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

inline fun <reified T> StompSession.subscribeOnMessage(destination: String): BlockingQueue<T> {
    val blockingQueue: BlockingQueue<T> = ArrayBlockingQueue(10)
    this.subscribe(destination, object : StompFrameHandler {
        override fun getPayloadType(headers: StompHeaders): Type = T::class.java

        override fun handleFrame(headers: StompHeaders, payload: Any?) {
            blockingQueue.add(payload as T)
        }
    })
    return blockingQueue
}