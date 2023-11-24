package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.*
import com.yandex.travelmap.exception.NotAuthorizedException
import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import com.yandex.travelmap.service.FriendService
import com.yandex.travelmap.service.UserService
import com.yandex.travelmap.util.handleExceptions
import com.yandex.travelmap.util.handleExceptionsResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val friendService: FriendService
) {
    private val currentUsername: String
        get() = SecurityContextHolder.getContext().authentication?.principal?.toString()
            ?: throw NotAuthorizedException()

    protected val currentUser: AppUser
        get() = userRepository.getUserOrThrow(currentUsername)

    @GetMapping("/stats")
    fun getStats(): UserStatsResponse {
        return userService.getUserStats(currentUser)
    }

    @GetMapping("/visited_countries")
    fun getVisitedCountries(): List<CountryResponse> {
        return userService.getVisitedCountries(currentUser, false)
    }

    @PutMapping("/visited_countries")
    fun addVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.addVisitedCountry(currentUser, request, false)
    }

    @DeleteMapping("/visited_countries")
    fun deleteVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.deleteVisitedCountry(currentUser, request, false)
    }

    @GetMapping("/desired_countries")
    fun getDesiredCountries(): List<CountryResponse> {
        return userService.getVisitedCountries(currentUser, true)
    }

    @PutMapping("/desired_countries")
    fun addDesiredCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.addVisitedCountry(currentUser, request, true)
    }

    @DeleteMapping("/desired_countries")
    fun deleteDesiredCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.deleteVisitedCountry(currentUser, request, true)
    }

    @PostMapping("/visited_cities")
    fun getVisitedCities(@RequestBody request: CitiesByCountryListRequest): List<CityResponse> {
        return userService.getVisitedCities(currentUser, request.iso)
    }

    @PutMapping("/visited_cities")
    fun addVisitedCities(@RequestBody request: VisitedCityRequest) {
        return userService.addVisitedCity(currentUser, request)
    }

    @DeleteMapping("/visited_cities")
    fun deleteVisitedCities(@RequestBody request: VisitedCityRequest) {
        return userService.deleteVisitedCity(currentUser, request)
    }

    @PostMapping("/friends/remove")
    fun removeFromFriends(@RequestBody request: FriendRequest) = handleExceptions {
        friendService.removeFriend(currentUser, request.friendName)
    }

    @PostMapping("/friends/request")
    fun getRequestsList(@RequestBody request: FriendRequestsRequest): List<String> {
        return userService.getRequestsList(currentUser, request.myRequests)
    }

    @PostMapping("/friends/request/send")
    fun sendFriendRequest(@RequestBody request: FriendRequest) = handleExceptions {
        friendService.sendFriendRequest(currentUser, request.friendName)
    }

    @PostMapping("/friends/request/cancel")
    fun cancelFriendRequest(@RequestBody request: FriendRequest) = handleExceptions {
        friendService.cancelFriendRequest(currentUser, request.friendName)
    }

    @PostMapping("/friends/request/accept")
    fun acceptFriendRequest(@RequestBody request: FriendRequest) = handleExceptions {
        friendService.handleFriendRequest(currentUser, request.friendName, accept = true)
    }

    @PostMapping("/friends/request/decline")
    fun declineFriendRequest(@RequestBody request: FriendRequest) = handleExceptions {
        friendService.handleFriendRequest(currentUser, request.friendName, accept = false)
    }

    @GetMapping("/friends")
    fun getFriendsList(): List<String> {
        return friendService.getFriendsList(currentUser)
    }

    @PostMapping("/friends/countries")
    fun getFriendCountries(@RequestBody request: FriendRequest) = handleExceptionsResponse {
        friendService.getFriendCountries(currentUser, request.friendName, false)
    }

    @PostMapping("/friends/countries/desired")
    fun getFriendDesiredCountries(@RequestBody request: FriendRequest) = handleExceptionsResponse {
        friendService.getFriendCountries(currentUser, request.friendName, true)
    }

    @PostMapping("/friends/countries/common")
    fun getFriendCommonCountries(@RequestBody request: FriendRequest) = handleExceptionsResponse {
        friendService.getFriendCommonCountries(currentUser, request.friendName, false)
    }

    @PostMapping("/friends/countries/common/desired")
    fun getFriendCommonDesiredCountries(@RequestBody request: FriendRequest) = handleExceptionsResponse {
        friendService.getFriendCommonCountries(currentUser, request.friendName, true)
    }

    @PostMapping("/friends/cities")
    fun getFriendCities(@RequestBody request: FriendCitiesRequest) = handleExceptionsResponse {
        friendService.getFriendCities(currentUser, request.friendName, request.iso)
    }

    @PostMapping("/friends/cities/common")
    fun getFriendCommonCities(@RequestBody request: FriendCitiesRequest) = handleExceptionsResponse {
        friendService.getFriendCommonCities(currentUser, request.friendName, request.iso)
    }

    @PostMapping("/friends/stats")
    fun getFriendStats(@RequestBody request: FriendRequest) = handleExceptionsResponse {
        friendService.getFriendStats(currentUser, request.friendName)
    }
}
