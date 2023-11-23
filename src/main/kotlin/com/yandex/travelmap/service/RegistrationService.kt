package com.yandex.travelmap.service

import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.exception.EmailNotValidException
import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import com.yandex.travelmap.util.EmailValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailValidator: EmailValidator
) {
    fun register(registrationRequest: RegistrationRequest) {
        val isValid = emailValidator.validate(registrationRequest.email)
        if (!isValid) {
            throw EmailNotValidException()
        }

        val emailExists = userRepository.findByEmail(registrationRequest.email).isPresent
        if (emailExists) {
            throw IllegalStateException("User with this email already exists")
        }

        val usernameExists = userRepository.findByUsername(registrationRequest.username).isPresent
        if (usernameExists) {
            throw IllegalStateException("User with this name already exists")
        }

        val encodedPassword = passwordEncoder.encode(registrationRequest.password)
        val appUser = AppUser(
            email = registrationRequest.email,
            username = registrationRequest.username,
            password = encodedPassword
        )

        userRepository.save(appUser)
    }
}
