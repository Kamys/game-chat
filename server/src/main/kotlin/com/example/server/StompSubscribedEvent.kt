package com.example.server

import org.bson.types.ObjectId
import org.springframework.context.ApplicationListener
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent

data class ChannelView(
    val id: String,
    val name: String,
)

data class UserStatusView(
    val activeChannels: List<ChannelView>,
    val currentChannelId: String?,
)

private val pathMatcher = AntPathMatcher()

data class SubscribeEventData(
    val event: SessionSubscribeEvent,
    val destination: String,
    val user: User,
    val variables: Map<String, String>,
)

abstract class SubscribeEventHandler {
    protected abstract val pattern: String

    fun tryHandle(event: SessionSubscribeEvent) {
        val headers = SimpMessageHeaderAccessor.wrap(event.message)
        val destination = headers.destination!!
        if (!canHandel(destination)) {
            return
        }
        val user = (event.user as UsernamePasswordAuthenticationToken).principal as User
        val variables = pathMatcher.extractUriTemplateVariables(pattern, destination)
        handle(
            SubscribeEventData(
                event = event,
                destination = destination,
                user = user,
                variables = variables,
            )
        )
    }

    abstract fun handle(event: SubscribeEventData)

    private fun canHandel(destination: String): Boolean {
        return pathMatcher.match(pattern, destination)
    }
}

@Component
class SubscribeOnChannel(
    private val messagingTemplate: SimpMessagingTemplate,
    private val channelRepository: ChannelRepository,
) : SubscribeEventHandler() {
    override val pattern = "/message/channel/{channelId}"

    override fun handle(event: SubscribeEventData) {
        val channelId = event.variables["channelId"]
        val channel = channelRepository.findById(ObjectId(channelId))
            ?: throw NotFoundException("Not found channel with id $channelId")
        channel.members = channel.members + event.user
        channelRepository.save(channel)
        val messages = MessageView("Server", "User '${event.user.name}' connect to channel ${channel.name}")
        messagingTemplate.convertAndSend(event.destination, messages)
    }
}

@Component
class SubscribeOnUserStatus(
    private val messagingTemplate: SimpMessagingTemplate,
    private val channelRepository: ChannelRepository,
) : SubscribeEventHandler() {
    override val pattern = "/user/status"

    override fun handle(event: SubscribeEventData) {
        val channels = channelRepository.findAll()

        val currentChannelId = channelRepository.findByMembersUsername(event.user.name)
            .firstOrNull()?.id

        val status = UserStatusView(
            activeChannels = channels.map {
                ChannelView(it.id.toString(), it.name)
            },
            currentChannelId = currentChannelId
        )
        messagingTemplate.convertAndSendToUser(event.user.name, "/status", status)
    }
}

@Component
class StompSubscribedEvent(
    val handlers: List<SubscribeEventHandler>,
) : ApplicationListener<SessionSubscribeEvent> {

    override fun onApplicationEvent(event: SessionSubscribeEvent) {
        handlers.forEach {
            it.tryHandle(event)
        }
    }
}

@Component
class ConnectListener : ApplicationListener<AbstractSubProtocolEvent> {
    override fun onApplicationEvent(event: AbstractSubProtocolEvent) {
        val headers = SimpMessageHeaderAccessor.wrap(event.message)
        println("SessionConnectEvent ${headers.messageType}: ${headers.destination}")
    }
}

@Component
class StompSendInterceptor : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
        if (accessor != null) {
            println("Sending STOMP message ${accessor.command}: Destination=${accessor.destination}")
        } else {
            println("Sending STOMP accessor is null")
        }
        return message
    }
}