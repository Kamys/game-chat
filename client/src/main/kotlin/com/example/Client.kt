package com.example

import org.springframework.messaging.simp.stomp.StompSession

class SubscriptionManager {
    private var current: StompSession.Subscription? = null

    fun setNew(subscription: StompSession.Subscription) {
        current?.unsubscribe()
        current = subscription
    }
}

class Client(private val server: GameServer) {
    private val channelSubscription = SubscriptionManager()

    fun subscribeMyStatus(): StompSession.Subscription {
        return server.subscribeMyStatus { userStatus ->
            println("Event MyStatus")
            if (userStatus.currentChannelId == null) {
                val channel = selectChannel(userStatus.activeChannels)
                subscribeChannel(channel)
            } else {
                subscribeChannel(userStatus.currentChannelId)
            }
        }
    }

    private fun subscribeChannel(channelId: String) {
        println("You inter in channel $channelId")
        channelSubscription.setNew(
            server.subscribeChannel(channelId) { handleChannelMessage(it) }
        )
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
