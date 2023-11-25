package com.yandex.travelmap.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.yandex.travelmap.config.JWTConfig
import com.yandex.travelmap.security.service.UserDetailsServiceImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.util.WebUtils
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtConfig: JWTConfig,
    private val userService: UserDetailsServiceImpl
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val cookie = WebUtils.getCookie(request, AUTH_COOKIE)
        val username = cookie?.let {
            getAuthenticationSubject(it.value)
        }
        if (username == null) {
            // User is not authenticated.
            chain.doFilter(request, response)
            return
        }

        val token = cookie.value
        val savedToken = userService.getUserOrThrow(username).getToken()
        if (token == savedToken) {
            val auth = UsernamePasswordAuthenticationToken(username, null, emptyList())
            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }

    private fun getAuthenticationSubject(token: String): String? = try {
        // Parse and verify the provided token.
        JWT.require(Algorithm.HMAC512(jwtConfig.secret))
            .build()
            .verify(token)
            .subject
    } catch (e: JWTVerificationException) {
        logger.debug(e.stackTraceToString())
        null
    }
}
