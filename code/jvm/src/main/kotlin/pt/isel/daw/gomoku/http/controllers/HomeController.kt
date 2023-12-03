package pt.isel.daw.gomoku.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriTemplate
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
            link(UriTemplate(Uris.Games.GET_MATCHMAKING_STATUS).expand("{mid}"), Rels.MATCHMAKING_STATUS)
            link(UriTemplate(Uris.Games.EXIT_MATCHMAKING_QUEUE).expand("{mid}"), Rels.EXIT_MATCHMAKING_QUEUE)
            link(URI(Uris.Games.GET_ALL_GAMES), Rels.GET_ALL_GAMES)
            link(UriTemplate(Uris.Games.GET_ALL_GAMES_BY_USER).expand("{uid}"), Rels.GET_ALL_GAMES_BY_USER)
            link(UriTemplate(Uris.Users.GET_USER_BY_ID).expand("{uid}"), Rels.USER)
            link(UriTemplate(Uris.Users.GET_STATS_BY_ID).expand("{uid}"), Rels.USER_STATS)
            link(URI(Uris.Users.GET_STATS_BY_USERNAME + "?name={query}"), Rels.USER_STATS)
            link(UriTemplate(Uris.Users.UPDATE_USER).expand("{uid}"), Rels.UPDATE_USER)
            link(URI(Uris.Users.LOGOUT), Rels.LOGOUT)
            link(UriTemplate(Uris.Games.GET_GAME_BY_ID).expand("{gid}"), Rels.GAME)
            link(UriTemplate(Uris.Games.PLAY).expand("{gid}"), Rels.PLAY)
            link(UriTemplate(Uris.Games.LEAVE).expand("{gid}"), Rels.LEAVE)
            link(URI(Uris.Users.LOGIN), Rels.LOGIN)
            link(URI(Uris.Users.REGISTER), Rels.REGISTER)
            link(URI(Uris.Games.GET_ALL_VARIANTS), Rels.GET_ALL_VARIANTS)
            link(UriTemplate(Uris.Games.GET_VARIANT_BY_NAME).expand("{name}"), Rels.VARIANT)
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