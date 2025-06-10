package ru.chimchima.travelmap.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PingController {
    @GetMapping("/ping")
    fun ping() = "ping ok\n"

    @GetMapping("/user/ping")
    fun authPing() = "User authenticated\n"
}
