package com.example.server

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.lang.annotation.Inherited
import java.util.*

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
@WithSecurityContext(factory = TestsSecurityContextFactory::class)
annotation class WithAuthenticatedUser(
    val username: String,
)

class TestsSecurityContextFactory(
    private val userRepository: UserRepository
) : WithSecurityContextFactory<WithAuthenticatedUser> {
    override fun createSecurityContext(annotation: WithAuthenticatedUser): SecurityContext {
        val user = userRepository.save(
            User(
                username = annotation.username,
                password = randomString()
            )
        )
        val authorities = listOf(SimpleGrantedAuthority("USER"))
        val auth = UsernamePasswordAuthenticationToken(user, "", authorities)

        return SecurityContextHolder.createEmptyContext().apply {
            authentication = auth
        }
    }
}
