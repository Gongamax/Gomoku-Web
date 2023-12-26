import { Game, GameState } from '../../../Domain/games/Game';
import { User } from '../../../Domain/users/User';

/**
 * Checks if it is the specified user's turn in the game.
 * @param username - The username of the user.
 * @param game - The game object.
 * @returns True if it is the user's turn, false otherwise.
 */
export function isMyTurn(username: string, game: Game): boolean {
  return (
    (game.state == GameState.NEXT_PLAYER_WHITE && game.userWhite.username == username) ||
    (game.state == GameState.NEXT_PLAYER_BLACK && game.userBlack.username == username)
  );
}

/**
 * Checks if the game is over.
 * @param game - The game object.
 * @returns A boolean indicating whether the game is over.
 */
export function isGameOver(game: Game): boolean {
  return (
    game.state == GameState.PLAYER_BLACK_WON || game.state == GameState.PLAYER_WHITE_WON || game.state == GameState.DRAW
  );
}

/**
 * Handles the winner of the game.
 * @param game - The game object.
 * @returns The user object of the winner, or null if it's a draw.
 */
export function handleWinner(game: Game): User | null {
  switch (game.state) {
    case GameState.PLAYER_WHITE_WON:
      return game.userWhite;
    case GameState.PLAYER_BLACK_WON:
      return game.userBlack;
    case GameState.DRAW:
      return null;
  }
}

/**
 * Determines the piece whose turn it is in the game.
 * 
 * @param game - The game object.
 * @returns The piece whose turn it is (Black or White), or null if the game state is invalid.
 */
export function isTurnOf(game: Game): string | null {
  switch (game.state) {
    case GameState.NEXT_PLAYER_BLACK:
      return 'Black';
    case GameState.NEXT_PLAYER_WHITE:
      return 'White';
    default:
      return null;
  }
}
