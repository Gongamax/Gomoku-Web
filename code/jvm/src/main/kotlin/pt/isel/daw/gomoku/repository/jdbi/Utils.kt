package pt.isel.daw.gomoku.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.daw.gomoku.repository.jdbi.mappers.*

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(EmailMapper())
    registerColumnMapper(IdMapper())
    registerColumnMapper(BoardMapper())
    registerColumnMapper(InstantMapper())

    return this
}
