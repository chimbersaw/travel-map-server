package ru.chimchima.travelmap.security.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.chimchima.travelmap.repository.UserRepository

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByUsername(username).orElseThrow {
            UsernameNotFoundException("No user with name $username exists")
        }
    }

    fun updateToken(username: String, token: String) {
        val user = userRepository.getUserOrThrow(username)
        user.setToken(token)
        userRepository.save(user)
    }

    fun removeToken(username: String) {
        val user = userRepository.getUserOrThrow(username)
        user.setToken(null)
        userRepository.save(user)
    }
}
