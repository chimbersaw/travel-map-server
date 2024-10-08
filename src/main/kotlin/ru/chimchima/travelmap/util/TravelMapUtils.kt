package ru.chimchima.travelmap.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ResponseStatusException

fun handleExceptions(block: () -> Unit): ResponseEntity<String> {
    return try {
        block()
        ResponseEntity(HttpStatus.OK)
    } catch (e: ResponseStatusException) {
        ResponseEntity(e.reason, e.statusCode)
    }
}

fun <R> handleExceptionsResponse(block: () -> R): ResponseEntity<Any> {
    return try {
        ResponseEntity(
            block(),
            HttpStatus.OK
        )
    } catch (e: ResponseStatusException) {
        ResponseEntity(e.message, e.statusCode)
    }
}
