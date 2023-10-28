package example

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import java.io.Serializable
import java.security.Principal

@Controller
class WebSocketController {

    data class Message(val username: String, val content: String)

    @MessageMapping("/hello/reply")
    @SendTo("/topic/reply")
    fun processMessageFromClient(content: String, request: Principal): Message {
        val user = ((request as UsernamePasswordAuthenticationToken).principal as User)
        return Message(user.username, content)
    }
}