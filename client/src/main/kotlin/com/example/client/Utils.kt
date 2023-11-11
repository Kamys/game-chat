package com.example.client

fun <T> selectOption(vararg options: Pair<T, String>): T {
    options.forEachIndexed { index, option ->
        println("${index+1}. ${option.second}")
    }
    while (true) {
        val userInput = readln().toIntOrNull()
        if (userInput == null) {
            println("Please, enter from 1 to ${options.size}")
            continue
        }
        val option = options.getOrNull(userInput - 1)
        if (option == null) {
            println("Please, enter from 1 to ${options.size}")
            continue
        }
        return option.first
    }
}

fun inputText(title: String): String {
    print("${title}: ")
    val result = readln()
    return result
}

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