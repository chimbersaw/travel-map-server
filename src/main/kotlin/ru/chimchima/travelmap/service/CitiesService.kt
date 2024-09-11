package ru.chimchima.travelmap.service

import org.springframework.stereotype.Service
import ru.chimchima.travelmap.dto.CityResponse
import ru.chimchima.travelmap.repository.CityRepository

@Service
class CitiesService(
    private val cityRepository: CityRepository
) {
    fun getCitiesByCountry(iso: String): List<CityResponse> {
        return cityRepository.findByCountryIso(iso)
            .map { CityResponse(iso, it.name) }
            .sortedBy { it.name }
    }
}
