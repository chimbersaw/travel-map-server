package com.yandex.travelmap.security

import com.yandex.travelmap.security.jwt.AUTH_COOKIE
import com.yandex.travelmap.security.jwt.JWTAuthenticationFilter
import com.yandex.travelmap.security.service.LogoutService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val authenticationProvider: AuthenticationProvider,
    private val jwtAuthenticationFilter: JWTAuthenticationFilter,
    private val logoutService: LogoutService
) {
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        val origin = System.getenv("SITE_URL") ?: "http://localhost:3000"
        configuration.allowedOrigins = listOf(origin)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("authorization", "content-type", "x-auth-token")
        configuration.exposedHeaders = listOf("x-auth-token", "set-cookie")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authenticationProvider(authenticationProvider)
        http {
            csrf { disable() }
            cors { }
            authorizeRequests {
                authorize("/registration/confirm", permitAll)
                authorize("/registration", permitAll)
                authorize("/ping", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/api/cities", permitAll)
                authorize("/api/**", authenticated)
            }

            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(
                jwtAuthenticationFilter
            )

            logout {
                logoutUrl = "/logout"
                deleteCookies(AUTH_COOKIE)
                addLogoutHandler(logoutService)
                logoutSuccessHandler = LogoutSuccessHandler { _, response, _ ->
                    response.status = HttpServletResponse.SC_OK
                    response.writer.println("You are logged out")
                }
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }

        return http.build()
    }
}
