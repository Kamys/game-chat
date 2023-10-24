package example.auth


import example.User
import org.springframework.security.core.context.SecurityContextHolder

fun getCurrentUser(): User {
    val authentication = SecurityContextHolder.getContext().authentication
    return authentication.principal as User
}