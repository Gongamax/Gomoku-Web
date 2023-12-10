import { User } from '../users/User';
import { Variant } from './Variant';

export type Game = {
  id: number;
  players: User[];
  board: Board;
  state: string;
  variant: Variant;
};

export enum GameState {
  SWAPPING_PIECES = 'SWAPPING_PIECES',
  NEXT_PLAYER_BLACK = 'NEXT_PLAYER_BLACK',
  NEXT_PLAYER_WHITE = 'NEXT_PLAYER_WHITE',
  PLAYER_BLACK_WON = 'PLAYER_BLACK_WON',
  PLAYER_WHITE_WON = 'PLAYER_WHITE_WON',
  DRAW = 'DRAW',
}
