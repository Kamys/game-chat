package com.example.server

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DatabaseSetup(
    private val channelRepository: ChannelRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        if (channelRepository.findAll().size != 0) {
            return
        }

        channelRepository.save(Channel(
            name = "Channel 1",
            members = emptySet(),
        ))
        channelRepository.save(Channel(
            name = "Channel 2",
            members = emptySet(),
        ))
    }
}