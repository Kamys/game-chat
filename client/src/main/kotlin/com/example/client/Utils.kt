package com.example.client

fun tryParsePrivateMessage(messages: String): Pair<String, String>? {
    val regexp = "@(\\w+)\\s(.*)".toRegex()
    if (!regexp.matches(messages)) {
        return null
    }
    val groupValues = regexp.find(messages)?.groupValues!!
    val username = groupValues[1]
    val messagesText = groupValues[2]
    return Pair(username, messagesText)
}