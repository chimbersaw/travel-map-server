package com.yandex.travelmap.repository

import com.yandex.travelmap.exception.UserNotFoundException
import com.yandex.travelmap.model.AppUser
import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
interface UserRepository : JpaRepository<AppUser, Long> {
    fun findByUsername(username: String): Optional<AppUser>

    fun findByEmail(username: String): Optional<AppUser>

    fun getUserOrThrow(username: String): AppUser
}

@Suppress("unused")
@Component
private class UserRepositoryImpl(@Lazy private val userRepository: UserRepository) {
    fun getUserOrThrow(username: String): AppUser = userRepository.findByUsername(username).orElseThrow {
        UserNotFoundException("No user with name $username exists")
    }
}
