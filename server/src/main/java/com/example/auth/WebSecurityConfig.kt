package com.example.auth

import com.example.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Service
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken


@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        return org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password(user.password)
            .authorities(emptyList())
            .build()
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
    private val tokenService: TokenService,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/login", permitAll)
                authorize("/register", permitAll)
                authorize(anyRequest, authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            oauth2ResourceServer { jwt { } }
            authenticationManager = AuthenticationManager {
                val jwt = it as BearerTokenAuthenticationToken
                val user = tokenService.parseToken(jwt.token)
                val authorities = listOf(SimpleGrantedAuthority("USER"))
                UsernamePasswordAuthenticationToken(user, "", authorities)
            }
            csrf {
                disable()
            }
        }
        return http.build()
    }
}