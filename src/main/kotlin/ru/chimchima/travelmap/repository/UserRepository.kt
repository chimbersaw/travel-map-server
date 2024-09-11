package ru.chimchima.travelmap.repository

import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.chimchima.travelmap.exception.UserNotFoundException
import ru.chimchima.travelmap.model.AppUser
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
