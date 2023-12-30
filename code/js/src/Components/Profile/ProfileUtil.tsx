import { GameOutputModel } from '../../Service/games/models/GameModelsUtil';
import { GetStatsOutput } from '../../Service/users/models/GetStatsOutput';
import { GetAllGamesByUserOutput } from '../../Service/games/models/GetUserGamesOutput';
import { GameState } from '../../Domain/games/Game';

/**
 * `calculateResult` is a function that calculates the result of a game for a given user.
 * It checks the game state and the user's ID to determine whether the user won, lost, or if the game is still in progress.
 *
 * @param {GameOutputModel} game - The game to calculate the result for.
 * @param {number} userId - The ID of the user to calculate the result for.
 * @returns {string} - The result of the game ('WIN', 'LOSS', 'DRAW', or 'IN PROGRESS').
 */
export function calculateResult(game: GameOutputModel, userId: number): string {
  switch (game.state) {
    case GameState.PLAYER_WHITE_WON:
      return game.userWhite.id.value === userId ? 'WIN' : 'LOSS';
    case GameState.PLAYER_BLACK_WON:
      return game.userBlack.id.value === userId ? 'WIN' : 'LOSS';
    case GameState.DRAW:
      return 'DRAW';
    default:
      return 'IN PROGRESS';
  }
}

export function convertToDomainUser(response: GetStatsOutput) {
  return {
    username: response.properties.username,
    wins: response.properties.wins,
    losses: response.properties.losses,
    draws: response.properties.gamesPlayed - (response.properties.wins + response.properties.losses),
    gamesPlayed: response.properties.gamesPlayed,
  };
}

export function convertToDomainMatchHistory(response: GetAllGamesByUserOutput, userId: number) {
  return response.entities.map(entity => {
    const game = entity.properties as unknown as GameOutputModel;
    const opponent = game.userBlack.id.value === userId ? game.userWhite : game.userBlack;
    const result = calculateResult(game, userId);
    return {
      opponent: opponent,
      result: result,
    };
  });
}
