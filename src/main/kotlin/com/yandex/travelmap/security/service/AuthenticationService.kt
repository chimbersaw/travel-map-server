package com.yandex.travelmap.security.service

import com.yandex.travelmap.dto.LoginRequest
import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.exception.EmailNotValidException
import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import com.yandex.travelmap.security.jwt.AUTH_COOKIE
import com.yandex.travelmap.security.jwt.JWTService
import com.yandex.travelmap.util.EmailValidator
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

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

        val cookie = Cookie(AUTH_COOKIE, token)
        cookie.secure = true
        response.addCookie(cookie)
        val header = response.getHeader(HttpHeaders.SET_COOKIE)
        response.setHeader(HttpHeaders.SET_COOKIE, "$header; SameSite=None")
    }
}
