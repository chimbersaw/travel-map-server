package com.yandex.travelmap.security.service

import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByUsername(username).orElseThrow {
            UsernameNotFoundException("No user with name $username exists")
        }
    }

    fun getUserOrThrow(username: String): AppUser {
        return userRepository.getUserOrThrow(username)
    }

    fun updateToken(username: String, token: String?) {
        val user = userRepository.getUserOrThrow(username)
        user.setToken(token)
        userRepository.save(user)
    }
}
