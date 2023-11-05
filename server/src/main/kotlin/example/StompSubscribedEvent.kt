package example

import org.springframework.context.ApplicationListener
import org.springframework.data.repository.CrudRepository
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
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
        val channel = channelRepository.findByIdOrThrow(channelId)
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