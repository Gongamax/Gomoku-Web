package pt.isel.daw.gomoku.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriTemplate
import pt.isel.daw.gomoku.http.media.siren.SirenModel
import pt.isel.daw.gomoku.http.media.siren.siren
import pt.isel.daw.gomoku.http.model.HomeOutputModel
import pt.isel.daw.gomoku.http.model.SystemInfoOutputModel
import pt.isel.daw.gomoku.http.util.Rels
import pt.isel.daw.gomoku.http.util.Uris
import java.net.URI


@RestController
class HomeController {
    @GetMapping(Uris.SYSTEM_INFO)
    fun getSystemInfo(): ResponseEntity<*> = ResponseEntity.ok(
        siren(SystemInfoOutputModel()) {
            clazz("system-info")
            link(URI(Uris.SYSTEM_INFO), Rels.SELF)
            link(Uris.home(), Rels.HOME)
            link(URI(Uris.Users.AUTH_HOME), Rels.AUTH_HOME)
            requireAuth(false)
        }
    )

    @GetMapping(Uris.HOME)
    fun getHome(): ResponseEntity<*> = ResponseEntity.ok().header("Content-Type", SirenModel.SIREN_MEDIA_TYPE)
        .body(
            siren(HomeOutputModel()) {
                clazz("home")
                recipeLinks(UriTemplate(Uris.HOME), Rels.SELF)
                recipeLinks(UriTemplate(Uris.HOME), Rels.HOME)
                recipeLinks(UriTemplate(Uris.SYSTEM_INFO), Rels.SYSTEM_INFO)
                recipeLinks(UriTemplate(Uris.Users.RANKING_INFO + "?page=1"), Rels.RANKING_INFO)
                recipeLinks(UriTemplate(Uris.Users.GET_STATS_BY_USERNAME_FOR_RANKING + "?page=1"), Rels.GET_STATS_BY_USERNAME_FOR_RANKING)
                recipeLinks(UriTemplate(Uris.Users.AUTH_HOME), Rels.AUTH_HOME)
                recipeLinks(UriTemplate(Uris.Games.MATCHMAKING), Rels.MATCHMAKING)
                recipeLinks(UriTemplate(Uris.Games.GET_MATCHMAKING_STATUS), Rels.MATCHMAKING_STATUS)
                recipeLinks(UriTemplate(Uris.Games.EXIT_MATCHMAKING_QUEUE), Rels.EXIT_MATCHMAKING_QUEUE)
                recipeLinks(UriTemplate(Uris.Games.GET_ALL_GAMES + "?page=1"), Rels.GET_ALL_GAMES)
                recipeLinks(UriTemplate(Uris.Games.GET_ALL_GAMES_BY_USER + "?page=1"), Rels.GET_ALL_GAMES_BY_USER)
                recipeLinks(UriTemplate(Uris.Users.GET_USER_BY_ID), Rels.USER)
                recipeLinks(UriTemplate(Uris.Users.GET_STATS_BY_ID), Rels.USER_STATS)
                recipeLinks(UriTemplate(Uris.Users.GET_STATS_BY_USERNAME), Rels.USER_STATS_BY_USERNAME)
                recipeLinks(UriTemplate(Uris.Users.UPDATE_USER), Rels.UPDATE_USER)
                recipeLinks(UriTemplate(Uris.Users.LOGOUT), Rels.LOGOUT)
                recipeLinks(UriTemplate(Uris.Games.GET_GAME_BY_ID), Rels.GAME)
                recipeLinks(UriTemplate(Uris.Games.PLAY), Rels.PLAY)
                recipeLinks(UriTemplate(Uris.Games.LEAVE), Rels.LEAVE)
                recipeLinks(UriTemplate(Uris.Users.LOGIN), Rels.LOGIN)
                recipeLinks(UriTemplate(Uris.Users.REGISTER), Rels.REGISTER)
                recipeLinks(UriTemplate(Uris.Games.GET_ALL_VARIANTS), Rels.GET_ALL_VARIANTS)
                action("register", Uris.Users.register(), HttpMethod.POST, "application/x-www-form-urlencoded") {
                    textField("username")
                    textField("email")
                    textField("password")
                    requireAuth(false)
                }
                action("login", Uris.Users.login(), HttpMethod.POST, "application/x-www-form-urlencoded") {
                    textField("username")
                    textField("password")
                    requireAuth(false)
                }
                requireAuth(false)
            }
        )
}