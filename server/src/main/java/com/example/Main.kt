package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@ConfigurationPropertiesScan
@SpringBootApplication
class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}

@Controller
class WebSocketController {

    @MessageMapping("/hello/replay")
    @SendTo("/topic/reply")
    fun processMessageFromClient(content: String): String {
        return "Reply from server: $content"
    }
}
