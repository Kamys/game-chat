package com.example.server

import org.bson.types.ObjectId
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import java.security.Principal

data class MessageView(
    val from: String,
    val content: String,
)

@Controller
class WebSocketController(
    private val channelRepository: ChannelRepository,
    private val userRepository: UserRepository,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    @MessageMapping("/send/private/{username}")
    fun handlePrivateMessage(
        @DestinationVariable username: String,
        @Payload message: String,
        principal: Principal,
    ) {
        val user = userRepository.findByUsername(username)
            ?: throw NotFoundException("Failed found user with username $username")
        simpMessagingTemplate.convertAndSendToUser(
            user.username,
            "/message/private",
            MessageView(principal.name, message)
        )
        // Send private message for sender too
        simpMessagingTemplate.convertAndSendToUser(
            principal.name,
            "/message/private",
            MessageView(principal.name, message)
        )
    }

    @MessageMapping("/send/channel/{channelId}")
    fun handleChannelMessage(
        @DestinationVariable channelId: String,
        @Payload message: String,
        principal: Principal,
    ) {
        println("handleChannelMessage: $message")
        val channel = channelRepository.findById(ObjectId(channelId))
            ?: throw NotFoundException("Failed found channel with id $channelId")

        channel.isMemberOrError(principal.name)

        simpMessagingTemplate.convertAndSend(
            "/message/channel/${channelId}",
            MessageView(principal.name, message)
        )
    }

    @MessageMapping("/send/global")
    @SendTo("/message/global")
    fun handleGlobalMessage(@Payload message: String, principal: Principal): MessageView {
        return MessageView(principal.name, message)
    }
}