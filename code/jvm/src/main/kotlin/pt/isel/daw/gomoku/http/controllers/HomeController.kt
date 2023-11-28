package pt.isel.daw.gomoku.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.gomoku.http.media.siren.siren
import pt.isel.daw.gomoku.http.model.*
import pt.isel.daw.gomoku.http.util.Rels
import pt.isel.daw.gomoku.http.util.Uris
import java.net.URI


@RestController
class HomeController {
    @GetMapping(Uris.SYSTEM_INFO)
    fun getSystemInfo(): ResponseEntity<*> = ResponseEntity.ok(
       siren( SystemInfoOutputModel()){
              clazz("system-info")
              link(URI(Uris.SYSTEM_INFO), Rels.SELF)
              link(URI(Uris.HOME), Rels.HOME)
              link(URI(Uris.Users.AUTH_HOME), Rels.AUTH_HOME)
              requireAuth(false)
       }
    )

    @GetMapping(Uris.HOME)
    fun getHome(): ResponseEntity<*> = ResponseEntity.ok(
        siren(HomeOutputModel()) {
            clazz("home")
            link(URI(Uris.HOME), Rels.SELF)
            link(URI(Uris.HOME), Rels.HOME)
            link(URI(Uris.SYSTEM_INFO), Rels.SYSTEM_INFO)
            link(URI(Uris.Users.RANKING_INFO + "?page=0" ), Rels.RANKING_INFO)
            link(URI(Uris.Users.AUTH_HOME), Rels.AUTH_HOME)
            link(URI(Uris.Games.MATCHMAKING), Rels.MATCHMAKING)
            link(URI(Uris.Games.GET_MATCHMAKING_STATUS), Rels.MATCHMAKING_STATUS)
            link(URI(Uris.Games.EXIT_MATCHMAKING_QUEUE), Rels.EXIT_MATCHMAKING_QUEUE)
            link(URI(Uris.Games.GET_ALL_GAMES), Rels.GET_ALL_GAMES)
            link(URI(Uris.Games.GET_ALL_GAMES_BY_USER), Rels.GET_ALL_GAMES_BY_USER)
            link(URI(Uris.Users.GET_USER_BY_ID), Rels.USER)
            link(URI(Uris.Users.GET_STATS_BY_ID), Rels.USER_STATS)
            link(URI(Uris.Users.UPDATE_USER), Rels.UPDATE_USER)
            link(URI(Uris.Users.LOGOUT), Rels.LOGOUT)
            link(URI(Uris.Games.GET_GAME_BY_ID), Rels.GAME)
            link(URI(Uris.Games.PLAY), Rels.PLAY)
            link(URI(Uris.Games.LEAVE), Rels.LEAVE)
            action("register", Uris.Users.register(), HttpMethod.POST, "application/x-www-form-urlencoded"){
                textField("username")
                textField("email")
                textField("password")
                requireAuth(false)
            }
            action("login", Uris.Users.login(), HttpMethod.POST,"application/x-www-form-urlencoded"){
                textField("username")
                textField("password")
                requireAuth(false)
            }
        requireAuth(false)
        }
    )
}