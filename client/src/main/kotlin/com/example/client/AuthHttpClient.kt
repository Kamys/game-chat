package com.example.client

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AuthHttpClient {
    private val client = OkHttpClient()
    private val host = "http://localhost:8080"

    fun login(username: String, password: String): String {
        return authRequest("/login", username, password)
    }

    fun register(username: String, password: String): String {
        return authRequest("/register", username, password)
    }

    private fun authRequest(path: String, username: String, password: String): String {
        val requestBody = requestBody(username, password)
        val request = Request.Builder()
            .url("$host$path")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            return readTokenFromResponse(responseBody!!)
        } else {
            throw Exception("Failed to login, response with status ${response.code}: ${response.body?.string()}")
        }
    }

    private fun requestBody(username: String, password: String): RequestBody {
        return """
            { "username": "$username", "password": "$password}" }
        """.trimIndent().toRequestBody("application/json; charset=utf-8".toMediaType())
    }

    private fun readTokenFromResponse(responseBody: String): String {
        val regex = """"token"\s*:\s*"([^"]+)"""".toRegex()
        val matchResult = regex.find(responseBody)
        val result = matchResult?.groups?.get(1)?.value
        return result!!
    }
}