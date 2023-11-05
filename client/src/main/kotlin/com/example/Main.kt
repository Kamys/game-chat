package com.example

import java.util.concurrent.CompletableFuture

fun runApp(): CompletableFuture<Unit> {
    val clientSession = CompletableFuture<Unit>()
    val token = Authorisation.getToken()
    val session = StompClient.getStompSession(token)
    val server = GameServer(session)
    val client = Client(server)

    client.subscribeMyStatus()

    return clientSession
}

/*fun test() {
    println("You successful connected to server!")
    session.subscribe("/topic/reply", object : StompFrameHandler {
        override fun getPayloadType(headers: StompHeaders): Type {
            return MessageView::class.java
        }

        override fun handleFrame(headers: StompHeaders, payload: Any?) {
            val message = (payload as MessageView)
            println("${Chalk.on(message.from).cyan()}: ${message.content}")
        }
    })
    session.subscribe("/user/queue/private", object : StompFrameHandler {
        override fun getPayloadType(headers: StompHeaders): Type {
            return MessageView::class.java
        }

        override fun handleFrame(headers: StompHeaders, payload: Any?) {
            val message = (payload as MessageView)
            println("[Private] ${Chalk.on(message.from).cyan()}: ${message.content}")
        }
    })

    while (true) {
        val newMessage = readln()
        if (newMessage.equals("exit", ignoreCase = true)) {
            return
        }
        server.sendMessages(newMessage)
    }
}*/

fun main() {
    println("Start!")
    runApp().join()
    println("Stop")
}

