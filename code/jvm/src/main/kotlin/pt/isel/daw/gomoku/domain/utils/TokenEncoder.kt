package pt.isel.daw.gomoku.domain.utils

/**
 *  This interface is responsible for encoding tokens.
 * */

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
