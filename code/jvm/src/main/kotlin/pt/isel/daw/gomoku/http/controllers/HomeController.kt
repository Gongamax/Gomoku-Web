package pt.isel.daw.gomoku.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Uris


@RestController
class HomeController {
    @GetMapping(Uris.SYSTEM_INFO)
    fun getSystemInfo(): ResponseEntity<*> = ResponseEntity.ok(SystemInfoOutputModel())

    @GetMapping(Uris.HOME)
    fun getHome(): ResponseEntity<*> = ResponseEntity.ok(HomeOutputModel())
}