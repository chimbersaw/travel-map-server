package ru.chimchima.travelmap.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JWTConfig(
    @Value("\${jwt.secret}")
    val secret: String,

    @Value("\${jwt.expires}")
    val expires: Long
)
