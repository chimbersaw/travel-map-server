package com.yandex.travelmap.security.jwt

import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.security.service.UserDetailsServiceImpl
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val AUTH_COOKIE = "travelmap_auth"


class JWTAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtService: JWTService,
    private val userService: UserDetailsServiceImpl
) : UsernamePasswordAuthenticationFilter(authenticationManager) {
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val user = authResult.principal as? AppUser
            ?: throw IllegalArgumentException("authResult must be an instance of User")

        val token = jwtService.createJwtToken(user.username)
        userService.updateToken(user.username, token)

        val cookie = Cookie(AUTH_COOKIE, token)
        cookie.secure = true
        response.addCookie(cookie)
        val header = response.getHeader(HttpHeaders.SET_COOKIE)
        response.setHeader(HttpHeaders.SET_COOKIE, "$header; SameSite=None")

        chain.doFilter(request, response)
    }
}
