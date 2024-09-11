package ru.chimchima.travelmap.security.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils
import ru.chimchima.travelmap.security.jwt.AUTH_COOKIE
import ru.chimchima.travelmap.security.jwt.JWTService

@Component
class LogoutService(
    private val jwtService: JWTService,
    private val userDetailsService: UserDetailsServiceImpl
) : LogoutHandler {
    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        val cookie = WebUtils.getCookie(request, AUTH_COOKIE)
        val username = cookie?.let {
            jwtService.getAuthenticationSubject(it.value)
        }

        if (username != null) {
            userDetailsService.removeToken(username)
        }
    }
}
