package com.example.server

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class WebSocketControllerTest(
    private val channelRepository: ChannelRepository,
) : WebSocketTest() {

    @Test
    @WithAuthenticatedUser(username = "Ron")
    fun `should get access for authenticated user`() {
        val result = mockMvc.get("/private-data").andReturn()

        result.response.status.shouldBe(200)
        result.response.contentAsString.shouldBe("privateData for user: Ron")
    }

    @Test
    fun `should send message in channel`() {
        val channel = channelRepository.save(
            Channel(name = randomString(), members = emptySet())
        )
        val alex = createWebSocketConnection("Alex")
        val ron = createWebSocketConnection("Ron")
        val alexMessageQueue = alex.session.subscribeOnMessage<MessageView>("/message/channel/${channel.id}")
        alexMessageQueue.pollAndCheck("Server: User '${alex.user.name}' connect to channel ${channel.name}")

        val ronMessageQueue = ron.session.subscribeOnMessage<MessageView>("/message/channel/${channel.id}")
        alexMessageQueue.pollAndCheck("Server: User '${ron.user.name}' connect to channel ${channel.name}")

        val messageFromAlex = randomString()
        alex.session.send("/app/send/channel/${channel.id}", messageFromAlex)
        alexMessageQueue.pollAndCheck("${alex.user.username}: $messageFromAlex")

        ronMessageQueue.pollAndCheck("Server: User '${ron.user.name}' connect to channel ${channel.name}")
        ronMessageQueue.pollAndCheck("${alex.user.username}: $messageFromAlex")
    }

    @Test
    fun `should send private message`() {
        val recipient = createWebSocketConnection()
        val privateMessageQueue = recipient.session.subscribeOnMessage<MessageView>("/user/message/private")

        val sender = createWebSocketConnection()
        val messageText = randomString()
        sender.session.send("/app/send/private/${recipient.user.username}", messageText)

        privateMessageQueue.pollAndCheck("${sender.user.username}: $messageText")
    }

    @Test
    fun `server should send user status after subscribe`() {
        val (session, user) = createWebSocketConnection()
        val channel = channelRepository.save(
            Channel(name = randomString(), members = emptySet())
        )
        session.subscribeOnMessage<MessageView>("/message/channel/${channel.id}")
        val privateMessageQueue = session.subscribeOnMessage<UserStatusView>("/user/status")
        val status = privateMessageQueue.poll(1, TimeUnit.SECONDS).shouldNotBeNull()
        status.currentChannelId.shouldBe(channel.id)
        status.activeChannels.first().id.shouldBe(channel.id)
    }

    private fun BlockingQueue<MessageView>.pollAndCheck(expected: String) {
        this.poll(1, TimeUnit.SECONDS)
            .shouldNotBeNull()
            .toString()
            .shouldBe(expected)
    }
}