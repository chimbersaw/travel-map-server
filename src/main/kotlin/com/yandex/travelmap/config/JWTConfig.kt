package com.yandex.travelmap.config

import org.springframework.context.annotation.Configuration

@Configuration
class JWTConfig {
    val secret: String by lazy {
        System.getenv("JWT_SECRET") ?: "default_JWT_secret"
    }

    val expirationTime: Long by lazy {
        System.getenv("JWT_EXPIRES")?.toLong() ?: 1209600000L
    }
}
