package pt.isel.daw.gomoku.http.util

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    private const val PREFIX = "/api"
    const val HOME = PREFIX
    const val SYSTEM_INFO = "$PREFIX/system"
    fun home(): URI = URI(HOME)
    fun systemInfo(): URI = URI(SYSTEM_INFO)
    object Users {
        private const val USERS = "/users"
        const val CREATE_USER = "$PREFIX$USERS"
        const val TOKEN = "$PREFIX$USERS/token"
        const val LOGOUT = "$PREFIX/logout"
        const val GET_USER_BY_ID = "$PREFIX$USERS/{id}"
        const val AUTH_HOME = "$PREFIX/me"
        const val RANKING_INFO = "$PREFIX/ranking"
        const val GET_STATS_BY_ID = "$PREFIX/stats/{id}"

        fun getUsersById(id: Int) = UriTemplate(GET_USER_BY_ID).expand(id)
        fun getStatsById(id: Int) = UriTemplate(GET_STATS_BY_ID).expand(id)
        fun authHome(): URI = URI(HOME)
        fun login(): URI = URI(TOKEN)
        fun register(): URI = URI(CREATE_USER)
        fun rankingInfo(): URI = URI(RANKING_INFO)
    }

    object Games {
        private const val GAMES = "/games"
        const val CREATE_GAME = "$PREFIX$GAMES"
        const val GET_GAME_BY_ID = "$PREFIX$GAMES/{id}"
        const val PLAY = "$PREFIX$GAMES/{id}/play"
        const val MATCHMAKING = "$PREFIX$GAMES/matchmaking"
        const val LEAVE = "$PREFIX$GAMES/{id}/leave"
        const val GET_ALL_GAMES = "$PREFIX$GAMES"
        const val GET_ALL_GAMES_BY_USER = "$PREFIX$GAMES/user"
        const val EXIT_MATCHMAKING_QUEUE = "$PREFIX$GAMES/matchmaking/exit"

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
