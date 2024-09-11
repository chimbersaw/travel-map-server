package ru.chimchima.travelmap.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils
import ru.chimchima.travelmap.repository.UserRepository

const val AUTH_COOKIE = "travelmap_auth"

@Component
class JWTAuthenticationFilter(
    private val jwtService: JWTService,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
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
        val savedToken = userRepository.getUserOrThrow(username).getToken()
        if (token == savedToken) {
            val auth = UsernamePasswordAuthenticationToken(username, null, emptyList())
            auth.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }
}
