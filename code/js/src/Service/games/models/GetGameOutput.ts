import { SirenModel } from '../../media/siren/SirenModel';
import { GameOutputModel } from './GameModelsUtil';

interface GameGetByIdOutputModel {
  game: GameOutputModel;
  pollingTimeOut: number;
}

export type GetGameOutput = SirenModel<GameGetByIdOutputModel>;
