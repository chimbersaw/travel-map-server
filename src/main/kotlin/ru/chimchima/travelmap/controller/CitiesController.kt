package ru.chimchima.travelmap.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.chimchima.travelmap.dto.CitiesByCountryListRequest
import ru.chimchima.travelmap.dto.CityResponse
import ru.chimchima.travelmap.service.CitiesService

@RestController
@RequestMapping("/api/cities")
class CitiesController(
    private val citiesService: CitiesService
) {
    @PostMapping
    fun getCitiesByCountry(@RequestBody request: CitiesByCountryListRequest): List<CityResponse> {
        return citiesService.getCitiesByCountry(request.iso)
    }
}
