package pt.isel.daw.gomoku.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    const val PREFIX = "/api"
    const val HOME = PREFIX

    fun home(): URI = URI(HOME)

    object  NonUsers {
        const val SYSTEM_INFO = "$PREFIX/system"
        const val RANKING_INFO = "$PREFIX/statistic"

        fun systemInfo(): URI = URI(SYSTEM_INFO)
        fun rankingInfo(): URI = URI(RANKING_INFO)
    }

    object Users {
        const val CREATE_USER = "$PREFIX/users"
        const val TOKEN = "$PREFIX/users/token"
        const val LOGOUT = "$PREFIX/logout"
        const val GET_USER_BY_ID = "$PREFIX/users/{id}"
        const val HOME = "$PREFIX/me"

        fun byId(id: Int) = UriTemplate(GET_USER_BY_ID).expand(id)
        fun home(): URI = URI(HOME)
        fun login(): URI = URI(TOKEN)
        fun register(): URI = URI(CREATE_USER)
    }

    object Games {
        const val CREATE_GAME = "$PREFIX/games"
        const val GET_GAME_BY_ID = "$PREFIX/games/{id}"
        const val PLAY = "$PREFIX/games/{id}/play"
        const val MATCHMAKING = "$PREFIX/games/matchmaking"
        const val LEAVE = "$PREFIX/games/{id}/leave"
        const val GET_ALL_GAMES = "$PREFIX/games"
        const val GET_ALL_GAMES_BY_USER = "$PREFIX/games/user/{id}"
        const val EXIT_MATCHMAKING_QUEUE = "$PREFIX/games/matchmaking/exit"

        fun byId(id: Int) = UriTemplate(GET_GAME_BY_ID).expand(id)
        fun play(id : Int): URI = UriTemplate(PLAY).expand(id)
        fun create(): URI = URI(CREATE_GAME)

        fun matchmaking(): URI = URI(MATCHMAKING)

        fun exitMatchmakingQueue(): URI = URI(EXIT_MATCHMAKING_QUEUE)

        fun leave(id: Int): URI = UriTemplate(LEAVE).expand(id)

        fun getAllGames(): URI = URI(GET_ALL_GAMES)

        fun getAllGamesByUser(): URI = URI(GET_ALL_GAMES_BY_USER)
    }
}
