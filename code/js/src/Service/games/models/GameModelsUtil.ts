import { Variant } from './GetVariantsOutput';
import { User } from '../../../Domain/users/User';

export type GameOutputModel = {
  id: number;
  board: Board;
  userBlack: User;
  userWhite: User;
  state: string;
  variant: Variant;
  created: number;
};
