package pt.isel.daw.gomoku.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    const val PREFIX = "/api"
    const val HOME = PREFIX

    fun home(): URI = URI(HOME)

    object  NonUsers {
        const val SYSTEM_INFO = "$PREFIX/system"
        const val RANKING_INFO = "$PREFIX/ranks"

        fun systemInfo(): URI = URI(SYSTEM_INFO)
        fun rankingInfo(): URI = URI(RANKING_INFO)
    }

    object Users {
        const val CREATE = "$PREFIX/users"
        const val TOKEN = "$PREFIX/users/token"
        const val LOGOUT = "$PREFIX/logout"
        const val GET_BY_ID = "$PREFIX/users/{id}"
        const val HOME = "$PREFIX/me"

        fun byId(id: Int) = UriTemplate(GET_BY_ID).expand(id)
        fun home(): URI = URI(HOME)
        fun login(): URI = URI(TOKEN)
        fun register(): URI = URI(CREATE)
    }
}
