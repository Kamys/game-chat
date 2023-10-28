package example

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class WebSocketController {

    @MessageMapping("/hello/reply")
    @SendTo("/topic/reply")
    fun processMessageFromClient(content: String, request: Principal): String {
        val user = ((request as UsernamePasswordAuthenticationToken).principal as User)
        return "${user.username}: $content"
    }
}