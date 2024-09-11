package ru.chimchima.travelmap.util

import org.springframework.stereotype.Service

private val EMAIL_PATTERN = Regex("^[\\w-_.+]*[\\w-_.]@(\\w+\\.)+\\w+\\w$")

@Service
class EmailValidator {
    fun validate(email: String): Boolean {
        return email.matches(EMAIL_PATTERN)
    }
}
