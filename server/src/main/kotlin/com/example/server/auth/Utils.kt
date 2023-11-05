package com.example.server.auth


import com.example.server.User
import org.springframework.security.core.context.SecurityContextHolder

fun getCurrentUser(): User {
    val authentication = SecurityContextHolder.getContext().authentication
    return authentication.principal as User
}