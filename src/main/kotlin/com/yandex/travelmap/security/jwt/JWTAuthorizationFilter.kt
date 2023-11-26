package com.yandex.travelmap.security.jwt

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
    private val jwtService: JWTService,
    private val userService: UserDetailsServiceImpl
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val cookie = WebUtils.getCookie(request, AUTH_COOKIE)
        val username = cookie?.let {
            jwtService.getAuthenticationSubject(it.value)
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
}
