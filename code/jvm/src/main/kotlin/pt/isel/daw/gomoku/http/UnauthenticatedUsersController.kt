package pt.isel.daw.gomoku.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.domain.users.AuthenticatedUser
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.services.TokenCreationError
import pt.isel.daw.gomoku.services.UnauthenticatedUsersService
import pt.isel.daw.gomoku.services.UserCreationError
import pt.isel.daw.gomoku.services.UsersService
import pt.isel.daw.gomoku.utils.Failure
import pt.isel.daw.gomoku.utils.Success

/** This controller may or may not be used this way **/

@RestController
class UnauthenticatedUsersController(
    private val unauthenticatedUserService: UnauthenticatedUsersService
) {
    /** The following methods could return ResponseEntity<*> **/
    @GetMapping(Uris.NonUsers.SYSTEM_INFO)
    fun getSystemInfo(): SystemInfoOutputModel = SystemInfoOutputModel()

    @GetMapping(Uris.NonUsers.RANKING_INFO)
    fun getRankingInfo(): RankingInfoOutputModel = RankingInfoOutputModel()
}