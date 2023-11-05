package com.example.client

import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import java.lang.reflect.Type

data class ChannelView(
    val id: String,
    val name: String
)

data class UserStatusView(
    val activeChannels: List<ChannelView>,
    val currentChannelId: String?
)


data class MessageView(
    val from: String,
    val content: String,
)

class GameServer(
    private val session: StompSession
) {
    fun sendMessagesInChannel(channelId: String, message: String) {
        session.send("/app/send/channel/${channelId}", message)
    }

    fun sendPrivateMessages(username: String, message: String) {
        session.send("/app/send/private/${username}", message)
    }

    fun subscribeMyStatus(handle: (UserStatusView) -> Unit): StompSession.Subscription {
        return subscribe("/user/status", handle)
    }

    fun subscribeChannel(channelId: String, handle: (MessageView) -> Unit): StompSession.Subscription {
        return subscribe("/message/channel/${channelId}", handle)
    }

    fun subscribePrivateMessages(handle: (MessageView) -> Unit): StompSession.Subscription {
        return subscribe("/user/message/private", handle)
    }

    private inline fun <reified T> subscribe(destination: String, crossinline handler: (T) -> Unit): StompSession.Subscription {
        return session.subscribe(destination, object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type {
                return T::class.java
            }

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                handler(payload as T)
            }
        })
    }
}