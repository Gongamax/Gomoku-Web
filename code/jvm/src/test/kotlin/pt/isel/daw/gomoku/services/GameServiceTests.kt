package pt.isel.daw.gomoku.services

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.gomoku.Environment
import pt.isel.daw.gomoku.TestClock
import pt.isel.daw.gomoku.domain.games.GameDomain
import pt.isel.daw.gomoku.repository.jdbi.JdbiTransactionManager
import pt.isel.daw.gomoku.repository.jdbi.configureWithAppRequirements
import kotlin.random.Random

class GameServiceTests {




    companion object {

//        private fun createGamesService(
//            testClock: TestClock,
//        ) = GamesService(
//            JdbiTransactionManager(jdbi),
//            GameDomain(
//
//            ),
//            testClock
//        )
        private fun newTestUserName() = "user-${Math.abs(Random.nextLong())}"

        private val dbUrl = Environment.getDbUrl()

        private val jdbi = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(dbUrl)
            }
        ).configureWithAppRequirements()
    }
}