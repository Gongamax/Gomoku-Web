package pt.isel.daw.gomoku.http.util

import pt.isel.daw.gomoku.http.media.siren.LinkRelation

object Rels {

    private const val BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-02/tree/main/docs/rels/"

    val SELF = LinkRelation("self")

    val NEXT: LinkRelation = LinkRelation("next")

    val PREVIOUS: LinkRelation = LinkRelation("previous")

    val FIRST: LinkRelation = LinkRelation("first")

    val LAST: LinkRelation = LinkRelation("last")

    val HOME = LinkRelation(BASE_URL + "rels/home")

    val SYSTEM_INFO = LinkRelation(BASE_URL + "rels/system-info")

    val REGISTER = LinkRelation(BASE_URL + "rels/create-a-user")

    val LOGIN = LinkRelation(BASE_URL + "rels/login")

    val LOGOUT = LinkRelation(BASE_URL + "rels/logout")

    val USER = LinkRelation(BASE_URL + "rels/get-user-by-id")

    val AUTH_HOME = LinkRelation(BASE_URL + "rels/auth-home")

    val RANKING_INFO = LinkRelation(BASE_URL + "rels/ranking-info")

    val USER_STATS = LinkRelation(BASE_URL + "rels/get-stats-by-id")

    val UPDATE_USER = LinkRelation(BASE_URL + "rels/update-user")

    val CREATE_GAME = LinkRelation(BASE_URL + "rels/create-a-game")

    val GAME = LinkRelation(BASE_URL + "rels/get-game-by-id")

    val PLAY = LinkRelation(BASE_URL + "rels/play")

    val MATCHMAKING = LinkRelation(BASE_URL + "rels/matchmaking")

    val MATCHMAKING_STATUS = LinkRelation(BASE_URL + "rels/get-matchmaking-status")

    val LEAVE = LinkRelation(BASE_URL + "rels/leave")

    val GET_ALL_GAMES = LinkRelation(BASE_URL + "rels/get-all-games")

    val GET_ALL_GAMES_BY_USER = LinkRelation(BASE_URL + "rels/get-all-games-by-user")

    val EXIT_MATCHMAKING_QUEUE = LinkRelation(BASE_URL + "rels/exit-matchmaking-queue")

    val GET_ALL_VARIANTS = LinkRelation(BASE_URL + "rels/get-all-variants")

    val VARIANT = LinkRelation(BASE_URL + "rels/get-variant")
}