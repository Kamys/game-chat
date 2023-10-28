package com.example

import com.github.tomaslanger.chalk.Chalk

fun main() {
    println("This message is " + Chalk.on("IMPORTANT").red().underline())
    println(Chalk.on("Tets").cyan())
    Thread.sleep(1000);
    Runtime.getRuntime().exec("clear");
    readln()
}