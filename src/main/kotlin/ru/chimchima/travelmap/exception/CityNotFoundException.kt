package ru.chimchima.travelmap.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CityNotFoundException(reason: String) : ResponseStatusException(HttpStatus.NOT_FOUND, reason)
