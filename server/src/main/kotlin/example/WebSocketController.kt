package example

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class WebSocketController {

    @MessageMapping("/hello/replay")
    @SendTo("/topic/reply")
    fun processMessageFromClient(content: String): String {
        return "Reply from server: $content"
    }
}