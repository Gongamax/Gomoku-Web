package pt.isel.daw.gomoku.services

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import pt.isel.daw.gomoku.domain.utils.Token
import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.domain.users.UsersDomain
import pt.isel.daw.gomoku.repository.TransactionManager
import pt.isel.daw.gomoku.utils.Either
import pt.isel.daw.gomoku.utils.failure
import pt.isel.daw.gomoku.utils.success

/** Class for unauthenticated users may or may not need to be implemented **/

@Component
@Service
class UnauthenticatedUsersService {
    fun getSystem() {
        TODO()
    }

    fun getStatistics() {
        TODO()
    }
}