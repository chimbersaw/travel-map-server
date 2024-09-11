package ru.chimchima.travelmap.service

import org.springframework.stereotype.Service
import ru.chimchima.travelmap.dto.*
import ru.chimchima.travelmap.model.AppUser
import ru.chimchima.travelmap.repository.CityRepository
import ru.chimchima.travelmap.repository.CountryRepository
import ru.chimchima.travelmap.repository.UserRepository
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val countryRepository: CountryRepository,
    private val cityRepository: CityRepository
) {
    fun getVisitedCountries(user: AppUser, isDesire: Boolean): List<CountryResponse> {
        val countries = if (isDesire) {
            user.desiredCountries
        } else {
            user.visitedCountries
        }

        return countries.map { country ->
            CountryResponse(country.iso, country.name)
        }
    }

    fun addVisitedCountry(user: AppUser, countryRequest: VisitedCountryRequest, isDesire: Boolean) {
        val country = countryRepository.getCountryOrThrow(countryRequest.iso)

        if (isDesire) {
            user.desiredCountries.add(country)
        } else {
            user.visitedCountries.add(country)

            if (country in user.desiredCountries) {
                user.desiredCountries.remove(country)
            }
        }

        userRepository.save(user)
    }

    fun deleteVisitedCountry(user: AppUser, countryRequest: VisitedCountryRequest, isDesire: Boolean) {
        val country = countryRepository.getCountryOrThrow(countryRequest.iso)

        if (isDesire) {
            user.desiredCountries.remove(country)
        } else {
            user.visitedCountries.remove(country)
        }

        userRepository.save(user)
    }

    fun getVisitedCities(user: AppUser, iso: String?): List<CityResponse> {
        var cities = user.visitedCities.map { city ->
            CityResponse(city.country.iso, city.name)
        }

        if (iso != null) {
            cities = cities.filter { iso == it.iso }
        }

        return cities.sortedBy { it.name }
    }

    fun addVisitedCity(user: AppUser, cityRequest: VisitedCityRequest) {
        val city = cityRepository.getCityOrThrow(cityRequest.name, cityRequest.iso)
        user.visitedCities.add(city)
        userRepository.save(user)
    }

    fun deleteVisitedCity(user: AppUser, cityRequest: VisitedCityRequest) {
        val city = cityRepository.getCityOrThrow(cityRequest.name, cityRequest.iso)
        user.visitedCities.remove(city)
        userRepository.save(user)
    }

    fun getRequestsList(user: AppUser, myRequests: Boolean): List<String> {
        val requests = if (myRequests) {
            user.myRequestsList
        } else {
            user.requestsToMeList
        }

        return requests.map { it.username }
    }

    fun getUserStats(user: AppUser): UserStatsResponse {
        val cities = user.visitedCities
        val response = UserStatsResponse(
            username = user.username,
            countriesNumber = user.visitedCountries.size,
            totalCitiesNumber = cities.size,
            citiesStats = LinkedList(),
            totalCommonCities = 0,
            commonCountries = 0
        )

        for (country in user.visitedCountries) {
            val citiesCount = cities.count { it.country.iso == country.iso }
            response.citiesStats.add(
                CitiesStatistic(
                    iso = country.iso,
                    name = country.name,
                    citiesNumber = citiesCount
                )
            )
        }

        return response
    }
}
