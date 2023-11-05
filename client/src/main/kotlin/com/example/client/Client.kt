package com.example.client

import org.springframework.messaging.simp.stomp.StompSession

class Client(private val server: GameServer) {
    private var currentChanelSubscription: StompSession.Subscription? = null
    private var currentChannelId: String? = null

    fun handleSendMessages() {
        while (true) {
            val messages = readln()
            currentChannelId?.let {
                server.sendMessages(it, messages)
            }
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

    private fun subscribeChannel(channelId: String) {
        currentChanelSubscription?.unsubscribe()
        currentChanelSubscription = server.subscribeChannel(channelId) { handleChannelMessage(it) }
    }

    private fun selectChannel(activeChannels: List<ChannelView>): String {
        println("Please select channel:")
        return selectOption(
            *activeChannels.map { it.id to it.name }.toTypedArray()
        )
    }

    private fun handleChannelMessage(messageView: MessageView) {
        println("${messageView.from}: ${messageView.content}")
    }
}
