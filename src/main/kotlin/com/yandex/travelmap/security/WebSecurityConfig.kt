package com.yandex.travelmap.security

import com.yandex.travelmap.security.jwt.AUTH_COOKIE
import com.yandex.travelmap.security.jwt.JWTAuthenticationFilter
import com.yandex.travelmap.security.jwt.JWTAuthorizationFilter
import com.yandex.travelmap.security.jwt.JWTService
import com.yandex.travelmap.security.service.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.util.WebUtils
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
    private val jwtService: JWTService,
    private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {
    @Bean
    fun authenticationFilter(): JWTAuthenticationFilter {
        val authenticationFilter = JWTAuthenticationFilter(authenticationManager(), jwtService, userDetailsService)
        authenticationFilter.setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/login", "POST"))
        authenticationFilter.setAuthenticationManager(authenticationManagerBean())
        return authenticationFilter
    }

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

    override fun configure(http: HttpSecurity) {
        http {
            csrf { disable() }
            cors { }
            authorizeRequests {
                authorize("/registration/confirm", permitAll)
                authorize("/registration", permitAll)
                authorize("/health", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/api/cities", permitAll)
                authorize("/api/**", authenticated)
            }

            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }

            formLogin {
                loginProcessingUrl = "/login"
                authenticationSuccessHandler = AuthenticationSuccessHandler { _, response, _ ->
                    response.status = HttpServletResponse.SC_OK
                    response.writer.println("You are logged in")

                }
                authenticationFailureHandler = AuthenticationFailureHandler { _, response, _ ->
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.writer.println("Failed to log in")
                }
            }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(
                authenticationFilter()
            )

            addFilterBefore<JWTAuthenticationFilter>(
                JWTAuthorizationFilter(
                    authenticationManager(),
                    jwtService,
                    userDetailsService
                )
            )

            logout {
                logoutUrl = "/logout"
                logoutSuccessHandler = LogoutSuccessHandler { request, response, _ ->
                    response.writer.println("You are logged out")
                    val cookie = WebUtils.getCookie(request, AUTH_COOKIE)
                    val username = cookie?.let {
                        jwtService.getAuthenticationSubject(it.value)
                    }
                    if (username != null) {
                        userDetailsService.removeToken(username)
                    }
                }
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }
}
