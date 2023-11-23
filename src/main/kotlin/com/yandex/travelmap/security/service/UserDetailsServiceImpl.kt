package com.yandex.travelmap.security.service

import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return findByName(username)
    }

    fun findByName(username: String): AppUser {
        return userRepository.getUserOrThrow(username)
    }

    fun updateToken(username: String, token: String?) {
        val user = findByName(username)
        user.setToken(token)
        userRepository.save(user)
    }
}
