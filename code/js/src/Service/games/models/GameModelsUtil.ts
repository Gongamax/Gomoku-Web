import { User } from "../../../Domain/users/User";
import { Board } from "../../../Domain/games/Board";

export type GameOutputModel = {
  id: number;
  board: Board;
  userBlack: User;
  userWhite: User;
};

export enum GameState {
  SWAPPING_PIECES,
  NEXT_PLAYER_BLACK,
  NEXT_PLAYER_WHITE,
  PLAYER_BLACK_WON,
  PLAYER_WHITE_WON,
  DRAW,
}
