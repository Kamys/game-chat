package com.example.client

object Authorisation {
    private var httpClient = GameHttpClient()

    fun getToken(): String {
        println("Please, select option:")
        val option = selectOption(
            1 to "Login",
            2 to "Create new account"
        )

        while (true) {
            val username = inputText("User name")
            val password = inputText("Password")

            return if (option == 1) {
                httpClient.login(username, password)
            } else {
                httpClient.register(username, password)
            }
        }
    }
}