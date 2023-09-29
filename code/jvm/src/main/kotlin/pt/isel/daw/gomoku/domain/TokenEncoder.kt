package pt.isel.daw.gomoku.domain

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
