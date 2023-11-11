package com.example.server

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.web.servlet.get
import java.lang.reflect.Type
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class WebSocketControllerTest(
    private val channelRepository: ChannelRepository,
): WebSocketTest() {

    @Test
    @WithAuthenticatedUser(username = "Ron")
    fun getPrivateData() {
        val result = mockMvc.get("/private-data").andReturn()

        result.response.status.shouldBe(200)
        result.response.contentAsString.shouldBe("privateData for user: Ron")
    }

    @Test
    fun verifyWebSocketEndpoint() {
        val channel = channelRepository.save(
            Channel(name = randomString(), members = emptySet())
        )
        val (stompSession, user) = createWebSocketConnection()
        val stompSessionHandler = MyStompSessionHandler()
        stompSession.subscribe("/message/channel/${channel.id}", stompSessionHandler)
        val messageText = randomString()
        stompSession.send("/app/send/channel/${channel.id}", messageText)

        stompSessionHandler.blockingQueue.poll(1, TimeUnit.SECONDS)
            .shouldNotBeNull()
            .also {
                it.content.shouldBe("User '${user.name}' connect to channel ${channel.name}")
                it.from.shouldBe("Server")
            }

        stompSessionHandler.blockingQueue.poll(1, TimeUnit.SECONDS)
            .shouldNotBeNull()
            .also {
                it.content.shouldBe(messageText)
                it.from.shouldBe("Alex")
            }
    }
}

class MyStompSessionHandler : StompSessionHandlerAdapter() {

    val blockingQueue: BlockingQueue<MessageView> = ArrayBlockingQueue(1)

    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        println("Connected!")
    }

    override fun getPayloadType(headers: StompHeaders): Type {
        return MessageView::class.java
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        blockingQueue.add(payload as MessageView)
    }
}