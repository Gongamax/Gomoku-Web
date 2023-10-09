package pt.isel.daw.gomoku.domain.utils

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
