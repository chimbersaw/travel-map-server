package com.yandex.travelmap.service

import com.yandex.travelmap.dto.CityResponse
import com.yandex.travelmap.dto.CountryResponse
import com.yandex.travelmap.dto.UserStatsResponse
import com.yandex.travelmap.exception.WrongUserRelationException
import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class FriendService(
    private val userRepository: UserRepository,
    private val userService: UserService
) {
    private fun checkIsFriend(user: AppUser, friend: AppUser) {
        if (friend !in user.friendsList) {
            throw WrongUserRelationException("User $friend is not your friend")
        }
    }

    fun sendFriendRequest(user: AppUser, friendName: String) {
        val friend = userRepository.getUserOrThrow(friendName)

        if (user == friend) {
            throw WrongUserRelationException("Can't send friend request to self")
        }

        if (friend in user.friendsList) {
            throw WrongUserRelationException("User is already a friend")
        }

        if (friend in user.myRequestsList) {
            throw WrongUserRelationException("Request already sent")
        }

        if (friend in user.requestsToMeList) {
            handleFriendRequest(user, friendName, accept = true)
        } else {
            user.myRequestsList.add(friend)
        }

        userRepository.save(user)
        userRepository.save(friend)
    }

    fun handleFriendRequest(user: AppUser, friendName: String, accept: Boolean) {
        val friend = userRepository.getUserOrThrow(friendName)

        if (friend !in user.requestsToMeList) {
            throw WrongUserRelationException("No requests from user $friendName")
        }

        user.requestsToMeList.remove(friend)
        friend.myRequestsList.remove(user)

        if (accept) {
            user.friendsList.add(friend)
            friend.friendsList.add(user)
        }

        userRepository.save(user)
        userRepository.save(friend)
    }

    fun cancelFriendRequest(user: AppUser, friendName: String) {
        val friend = userRepository.getUserOrThrow(friendName)

        if (friend !in user.myRequestsList) {
            throw WrongUserRelationException("No requests to user $friendName")
        }

        user.myRequestsList.remove(friend)
        userRepository.save(user)
    }

    fun removeFriend(user: AppUser, friendName: String) {
        val friend = userRepository.getUserOrThrow(friendName)
        checkIsFriend(user, friend)

        user.friendsList.remove(friend)
        friend.friendsList.remove(user)

        userRepository.save(user)
        userRepository.save(friend)
    }

    fun getFriendsList(user: AppUser): List<String> {
        return user.friendsList.map { it.username }.toList()
    }

    fun getFriendCountries(user: AppUser, friendName: String, isDesire: Boolean): List<CountryResponse> {
        val friend = userRepository.getUserOrThrow(friendName)
        checkIsFriend(user, friend)
        return userService.getVisitedCountries(friend, isDesire)
    }

    fun getFriendCommonCountries(user: AppUser, friendName: String, isDesire: Boolean): List<CountryResponse> {
        val friendsCountries = getFriendCountries(user, friendName, isDesire)
        val myCountries = userService.getVisitedCountries(user, isDesire)
        return myCountries.filter { it in friendsCountries }
    }

    fun getFriendCities(user: AppUser, friendName: String, iso: String?): List<CityResponse> {
        val friend = userRepository.getUserOrThrow(friendName)
        checkIsFriend(user, friend)
        return userService.getVisitedCities(friend, iso)
    }

    fun getFriendCommonCities(user: AppUser, friendName: String, iso: String?): List<CityResponse> {
        val friendsCities = getFriendCities(user, friendName, iso)
        val myCities = userService.getVisitedCities(user, iso)
        return myCities.filter { it in friendsCities }
    }

    fun getFriendStats(user: AppUser, friendName: String): UserStatsResponse {
        val friend = userRepository.getUserOrThrow(friendName)
        checkIsFriend(user, friend)

        val statsResponse = userService.getUserStats(friend)
        statsResponse.totalCommonCities = getFriendCommonCities(user, friendName, iso = null).size
        statsResponse.commonCountries = getFriendCommonCountries(user, friendName, isDesire = false).size
        return statsResponse
    }
}
