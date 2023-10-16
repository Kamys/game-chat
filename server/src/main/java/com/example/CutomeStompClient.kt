package com.example

import okhttp3.*

fun WebSocket.sendText(text: String) {
    val request = text + "\n\n\u0000"
    println("====Request====")
    println(request)
    println("============")
    this.send(request)
}

class MyListener() : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        val connectFrame = """
                    CONNECT
                    accept-version:1.1,1.0
                    heart-beat:10000,10000
                """.trimIndent()
        webSocket.sendText(connectFrame)

        webSocket.sendText(
            """
                    SUBSCRIBE
                    id:0
                    destination:/topic/reply
                    ack:client
                """.trimIndent()
        )
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        println("====Response====")
        println(text)
        println("============")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Closing: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("Failure: ${t.localizedMessage}")
    }
}

fun main() {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("ws://localhost:8080/ws/pp/123/websocket")
        .build()
    val listener = MyListener()
    val ws = client.newWebSocket(request, listener)
    while (true) {
        val userInput = readlnOrNull()
        ws.sendText(
            """
                    SEND
                    destination:/app/hello/replay
                    accept-version:1.2
                    host:localhost
                    content-type:text/plain
                    
                    $userInput
                """.trimIndent()
        )
    }
}