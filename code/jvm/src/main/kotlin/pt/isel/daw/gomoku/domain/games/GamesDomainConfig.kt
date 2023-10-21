package pt.isel.daw.gomoku.domain.games

import kotlin.time.Duration

/**
 * Represents the game configuration and variant rules.
 * @property variant the variant of the game.
 * @property openingRule the opening rule of the game.
 * @throws IllegalArgumentException if the board size is not greater than 0.
 * @throws IllegalArgumentException if the variant is not a valid game variant.
 * @throws IllegalArgumentException if the opening rule is not a valid opening rule.
 */
data class GamesDomainConfig(
    val timeout : Duration,
)