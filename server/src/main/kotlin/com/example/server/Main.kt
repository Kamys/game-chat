package com.example.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}