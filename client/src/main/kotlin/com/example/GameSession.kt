package com.example

import org.springframework.messaging.simp.stomp.StompSession

class GameServer(
    private val session: StompSession
) {
    fun sendMessages(message: String) {
        session.send("/app/hello/reply", message)
    }
}