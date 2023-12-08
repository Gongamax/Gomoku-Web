import { User } from '../../../Domain/users/User';
import { Board } from '../../../Domain/games/Board';
import { Variant } from './GetVariantsOutput';

export type GameOutputModel = {
  id: number;
  board: Board;
  userBlack: User;
  userWhite: User;
  state: string;
  variant: Variant;
  created: number;
};
