package pt.isel.daw.gomoku.http.util

import pt.isel.daw.gomoku.http.media.siren.LinkRelation

object Rels {

    private const val BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/"

    val SELF = LinkRelation("self")

    val NEXT: LinkRelation = LinkRelation("next")

    val PREVIOUS: LinkRelation = LinkRelation("previous")

    val FIRST: LinkRelation = LinkRelation("first")

    val LAST: LinkRelation = LinkRelation("last")

    val HOME = LinkRelation(BASE_URL + "home")

    val SYSTEM_INFO = LinkRelation(BASE_URL + "system-info")

    val REGISTER = LinkRelation(BASE_URL + "create-a-user")

    val LOGIN = LinkRelation(BASE_URL + "login")

    val LOGOUT = LinkRelation(BASE_URL + "logout")

    val USER = LinkRelation(BASE_URL + "get-user-by-id")

    val AUTH_HOME = LinkRelation(BASE_URL + "auth-home")

    val RANKING_INFO = LinkRelation(BASE_URL + "ranking-info")

    val GET_STATS_BY_USERNAME_FOR_RANKING = LinkRelation(BASE_URL + "get-stats-by-username-for-ranking")

    val USER_STATS = LinkRelation(BASE_URL + "get-user-stats")

    val USER_STATS_BY_USERNAME: LinkRelation = LinkRelation(BASE_URL + "get-user-stats-by-username")

    val UPDATE_USER = LinkRelation(BASE_URL + "update-user")

    val GAME = LinkRelation(BASE_URL + "get-game-by-id")

    val PLAY = LinkRelation(BASE_URL + "play")

    val MATCHMAKING = LinkRelation(BASE_URL + "matchmaking")

    val MATCHMAKING_STATUS = LinkRelation(BASE_URL + "get-matchmaking-status")

    val LEAVE = LinkRelation(BASE_URL + "leave")

    val GET_ALL_GAMES = LinkRelation(BASE_URL + "get-all-games")

    val GET_ALL_GAMES_BY_USER = LinkRelation(BASE_URL + "get-all-games-by-user")

    val EXIT_MATCHMAKING_QUEUE = LinkRelation(BASE_URL + "exit-matchmaking-queue")

    val GET_ALL_VARIANTS = LinkRelation(BASE_URL + "get-all-variants")
}