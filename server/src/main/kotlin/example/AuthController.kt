package example

import example.auth.TokenService
import example.auth.getCurrentUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class AuthController(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder,
) {

    @PostMapping("/login")
    fun login(@RequestBody payload: AuthRequest): AuthResponse {
        val user = userRepository.findByUsername(payload.username) ?: throw Exception("Login failed")

        if (!passwordEncoder.matches(payload.password, user.password)) {
            throw Exception("Login failed")
        }

        return AuthResponse(
            token = tokenService.createToken(user),
        )
    }

    @PostMapping("/register")
    fun register(@RequestBody payload: AuthRequest): AuthResponse {
        if (userRepository.existsByUsername(payload.username)) {
            throw Exception("Name already exists")
        }

        val user = User(
            username = payload.username,
            password = passwordEncoder.encode(payload.password),
        )

        val savedUser = userRepository.save(user)

        return AuthResponse(
            token = tokenService.createToken(savedUser),
        )
    }

    @GetMapping("/private-data")
    fun privateData(): String {
        val user = getCurrentUser()
        return "privateData for user: ${user.username}"
    }

    class AuthRequest(
        val username: String,
        val password: String,
    )

    class AuthResponse(
        val token: String,
    )
}