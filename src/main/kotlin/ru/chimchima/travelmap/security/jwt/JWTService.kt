package ru.chimchima.travelmap.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

@Service
class JWTService(private val jwtConfig: ru.chimchima.travelmap.config.JWTConfig) {
    private val logger = LogFactory.getLog(javaClass)
    private val algorithm by lazy {
        Algorithm.HMAC512(jwtConfig.secret)
    }

    fun getAuthenticationSubject(token: String): String? = try {
        JWT.require(algorithm)
            .build()
            .verify(token)
            .subject
    } catch (e: JWTVerificationException) {
        logger.debug(e.stackTraceToString())
        null
    }

    fun createJwtToken(username: String): String {
        return JWT.create()
            .withSubject(username)
            .withExpiresAt(Instant.now().plusMillis(jwtConfig.expires))
            .sign(algorithm)
    }

    fun getJwtExpirationTimeSeconds() = jwtConfig.expires.milliseconds.inWholeSeconds
}
