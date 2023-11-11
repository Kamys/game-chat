package com.example.client

object Authorisation {
    private var authHttpClient = AuthHttpClient()

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
                authHttpClient.login(username, password)
            } else {
                authHttpClient.register(username, password)
            }
        }
    }
}