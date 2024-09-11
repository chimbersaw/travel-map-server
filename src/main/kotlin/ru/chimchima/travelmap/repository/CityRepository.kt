package ru.chimchima.travelmap.repository

import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.chimchima.travelmap.exception.CityNotFoundException
import ru.chimchima.travelmap.model.City
import java.util.*

@Repository
@Transactional
interface CityRepository : JpaRepository<City, Long> {
    fun findByNameIgnoreCaseAndCountryIso(name: String, countryIso: String): Optional<City>

    fun findByCountryIso(iso: String): List<City>

    fun getCityOrThrow(name: String, countryIso: String): City
}

@Suppress("unused")
@Component
private class CityRepositoryImpl(@Lazy private val cityRepository: CityRepository) {
    fun getCityOrThrow(name: String, countryIso: String): City {
        return cityRepository.findByNameIgnoreCaseAndCountryIso(name, countryIso).orElseThrow {
            CityNotFoundException("No city with name $name in country $countryIso exists")
        }
    }
}
