package ru.chimchima.travelmap.security.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.chimchima.travelmap.dto.LoginRequest
import ru.chimchima.travelmap.dto.RegistrationRequest
import ru.chimchima.travelmap.exception.EmailNotValidException
import ru.chimchima.travelmap.model.AppUser
import ru.chimchima.travelmap.repository.UserRepository
import ru.chimchima.travelmap.security.jwt.AUTH_COOKIE
import ru.chimchima.travelmap.security.jwt.JWTService
import ru.chimchima.travelmap.util.EmailValidator

@Service
class AuthenticationService(
    private val jwtService: JWTService,
    private val userDetailsService: UserDetailsServiceImpl,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val emailValidator: EmailValidator
) {
    fun register(request: RegistrationRequest) {
        val isValid = emailValidator.validate(request.email)
        if (!isValid) {
            throw EmailNotValidException()
        }

        val emailExists = userRepository.findByEmail(request.email).isPresent
        if (emailExists) {
            throw IllegalStateException("User with this email already exists")
        }

        val usernameExists = userRepository.findByUsername(request.username).isPresent
        if (usernameExists) {
            throw IllegalStateException("User with this name already exists")
        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val appUser = AppUser(
            email = request.email,
            username = request.username,
            password = encodedPassword
        )

        userRepository.save(appUser)
    }

    fun login(request: LoginRequest, response: HttpServletResponse) {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )

        val token = jwtService.createJwtToken(request.username)
        userDetailsService.updateToken(request.username, token)

        val cookie = Cookie(AUTH_COOKIE, token).apply {
            secure = true
            isHttpOnly = true
            maxAge = jwtService.getJwtExpirationTimeSeconds().toInt()
            path = "/api"
            setAttribute("SameSite", "Lax")
        }
        response.addCookie(cookie)
    }
}
