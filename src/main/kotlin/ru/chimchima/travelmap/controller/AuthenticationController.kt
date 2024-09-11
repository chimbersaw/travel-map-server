package ru.chimchima.travelmap.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.chimchima.travelmap.dto.LoginRequest
import ru.chimchima.travelmap.dto.RegistrationRequest
import ru.chimchima.travelmap.exception.EmailNotValidException
import ru.chimchima.travelmap.security.service.AuthenticationService

@RestController
@RequestMapping
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegistrationRequest): ResponseEntity<String> {
        return try {
            authenticationService.register(request)
            ResponseEntity("Registration successful", HttpStatus.OK)
        } catch (e: IllegalStateException) {
            ResponseEntity("Registration failed: ${e.message}", HttpStatus.CONFLICT)
        } catch (e: EmailNotValidException) {
            ResponseEntity("Email not valid", HttpStatus.CONFLICT)
        }
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        return try {
            authenticationService.login(request, response)
            ResponseEntity("You are logged in", HttpStatus.OK)
        } catch (e: AuthenticationException) {
            ResponseEntity("Username or password is incorrect", HttpStatus.UNAUTHORIZED)
        }
    }
}
