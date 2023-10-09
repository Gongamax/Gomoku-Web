package pt.isel.daw.gomoku.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.daw.gomoku.repository.jdbi.mappers.BoardMapper
import pt.isel.daw.gomoku.repository.jdbi.mappers.InstantMapper
import pt.isel.daw.gomoku.repository.jdbi.mappers.PasswordValidationInfoMapper
import pt.isel.daw.gomoku.repository.jdbi.mappers.TokenValidationInfoMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(BoardMapper())
    registerColumnMapper(InstantMapper())

    return this
}
