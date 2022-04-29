package com.yandex.travelmap.service

import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.exception.EmailNotValidException
import com.yandex.travelmap.util.EmailValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RegistrationService(
    private val userService: UserService,
    private val emailValidator: EmailValidator
) {
    fun register(registrationRequest: RegistrationRequest) {
        val isEmailValid: Boolean = emailValidator.validate(registrationRequest.email)
        if (!isEmailValid) {
            throw EmailNotValidException()
        }
        userService.registerUser(registrationRequest)
    }
}
