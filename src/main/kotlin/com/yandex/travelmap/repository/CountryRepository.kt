package com.yandex.travelmap.repository

import com.yandex.travelmap.exception.CountryNotFoundException
import com.yandex.travelmap.model.Country
import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
interface CountryRepository : JpaRepository<Country, Long> {
    fun findByIso(iso: String): Optional<Country>

    fun getCountryOrThrow(iso: String): Country
}

@Suppress("unused")
@Component
private class CountryRepositoryImpl(@Lazy private val countryRepository: CountryRepository) {
    fun getCountryOrThrow(iso: String): Country = countryRepository.findByIso(iso).orElseThrow {
        CountryNotFoundException("No country with iso $iso exists")
    }
}
