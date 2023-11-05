package com.example.client

import java.util.concurrent.CompletableFuture

fun runApp(): CompletableFuture<Unit> {
    val clientSession = CompletableFuture<Unit>()
    val token = Authorisation.getToken()
    val session = StompClient.getStompSession(token)
    val server = GameServer(session)
    val client = Client(server)

    client.joinToChannel()
    client.subscribePrivateMessages()
    client.handleUserInput()

    return clientSession
}

fun main() {
    println("Start!")
    runApp().join()
    println("Stop")
}

