package pt.isel.daw.gomoku.domain.games

import kotlin.time.Duration

/**
 * Represents the game configuration and variant rules.
 * @property boardSize the size of the board.
 * @property variant the variant of the game.
 * @property openingRule the opening rule of the game.
 * @throws IllegalArgumentException if the board size is not greater than 0.
 * @throws IllegalArgumentException if the variant is not a valid game variant.
 * @throws IllegalArgumentException if the opening rule is not a valid opening rule.
 */
data class GamesDomainConfig(
    val timeout : Duration,
    val variant: Variant,
    val openingRule: OpeningRule
) {
    init {
        require(variant.boardDim.toInt() == 15 || variant.boardDim.toInt() == 19) { "boardSize must be either 15 or 19" }
        require(variant in Variant.values()) { "variant must be a valid game Variant" }
        require(openingRule in OpeningRule.values()) { "opening rule must be a valid Opening Rule" }
    }
}