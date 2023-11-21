package com.example.client

import org.springframework.messaging.simp.stomp.StompSession
import java.util.concurrent.CompletableFuture

class Client(private val chatSession: ChatSession) {
    private var currentChanelSubscription: StompSession.Subscription? = null
    private var currentChannelId: String? = null

    fun handleUserInput() {
        while (true) {
            val messages = readln()
            handleInputMessage(this.currentChannelId, messages)
        }
    }

    fun subscribePrivateMessages() {
        chatSession.subscribePrivateMessages {
            println("[Private] ${it.from}: ${it.content}")
        }
    }

    fun joinToChannel(): CompletableFuture<Unit> {
        val connectToChannel = CompletableFuture<Unit>()
        chatSession.subscribeMyStatus { userStatus ->
            println("Event MyStatus")
            val channelId = userStatus.currentChannelId ?: selectChannel(userStatus.activeChannels)
            subscribeChannel(channelId)
            currentChannelId = channelId
            connectToChannel.complete(Unit)
        }
        return connectToChannel
    }

    private fun handleInputMessage(currentChannelId: String?, message: String) {
        if (currentChannelId == null) {
            println("Failed to send message. You are not connected to a channel")
            return
        }

        tryParsePrivateMessage(message)?.let { (username, message) ->
            chatSession.sendPrivateMessages(username, message)
            return
        }

        chatSession.sendMessagesInChannel(currentChannelId, message)
    }

    private fun subscribeChannel(channelId: String) {
        currentChanelSubscription?.unsubscribe()
        currentChanelSubscription = chatSession.subscribeChannel(channelId) {
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
