import { User } from '../users/User';
import { Variant } from './Variant';
import { Board } from './Board';
import { GameOutputModel } from '../../Service/games/models/GameModelsUtil';

/**
 * Type representing a game.
 * @type {Object} Game
 * @property {number} id - The unique identifier of the game.
 * @property {User} userBlack - The user playing as black.
 * @property {User} userWhite - The user playing as white.
 * @property {Board} board - The current state of the game board.
 * @property {string} state - The current state of the game.
 * @property {Variant} variant - The variant of the game being played.
 */
export type Game = {
  id: number;
  userBlack: User;
  userWhite: User;
  board: Board;
  state: string;
  variant: Variant;
};

/**
 * Enum representing the possible states of a game.
 * @enum {string}
 */
export enum GameState {
  SWAPPING_PIECES = 'SWAPPING_PIECES',
  NEXT_PLAYER_BLACK = 'NEXT_PLAYER_BLACK',
  NEXT_PLAYER_WHITE = 'NEXT_PLAYER_WHITE',
  PLAYER_BLACK_WON = 'PLAYER_BLACK_WON',
  PLAYER_WHITE_WON = 'PLAYER_WHITE_WON',
  DRAW = 'DRAW',
}

/**
 * Function to convert a game output model to a domain game.
 * @param {GameOutputModel} gameOutputModel - The game output model to be converted.
 * @returns {Game} The converted domain game.
 */
export function convertToDomainGame(gameOutputModel: GameOutputModel): Game {
  return {
    id: gameOutputModel.id,
    userBlack: gameOutputModel.userBlack,
    userWhite: gameOutputModel.userWhite,
    board: gameOutputModel.board,
    state: gameOutputModel.state,
    variant: gameOutputModel.variant,
  };
}
