package com.example.client

import org.springframework.messaging.simp.stomp.StompSession

class Client(private val server: GameServer) {
    private var currentChanelSubscription: StompSession.Subscription? = null
    private var currentChannelId: String? = null

    fun handleUserInput() {
        while (true) {
            val messages = readln()
            handleInputMessage(this.currentChannelId, messages)
        }
    }

    fun subscribePrivateMessages() {
        server.subscribePrivateMessages {
            println("[Private] ${it.from}: ${it.content}")
        }
    }

    fun joinToChannel() {
        server.subscribeMyStatus { userStatus ->
            println("Event MyStatus")
            val channelId = userStatus.currentChannelId ?: selectChannel(userStatus.activeChannels)
            currentChannelId = userStatus.currentChannelId
            subscribeChannel(channelId)
        }
    }

    private fun handleInputMessage(currentChannelId: String?, message: String) {
        if (currentChannelId == null) {
            println("Failed to send message. You are not connected to a channel")
            return
        }

        tryParsePrivateMessage(message)?.let { (username, message) ->
            server.sendPrivateMessages(username, message)
            return
        }

        server.sendMessagesInChannel(currentChannelId, message)
    }

    private fun subscribeChannel(channelId: String) {
        currentChanelSubscription?.unsubscribe()
        currentChanelSubscription = server.subscribeChannel(channelId) {
            println("${it.from}: ${it.content}")
        }
    }

    private fun selectChannel(activeChannels: List<ChannelView>): String {
        println("Please select channel:")
        return selectOption(
            *activeChannels.map { it.id to it.name }.toTypedArray()
        )
    }
}
