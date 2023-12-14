import { Variant } from './GetVariantsOutput';
import { User } from '../../../Domain/users/User';
import { Board } from '../../../Domain/games/Board';

export type GameOutputModel = {
  id: number;
  board: Board;
  userBlack: User;
  userWhite: User;
  state: string;
  variant: Variant;
  created: number;
};
