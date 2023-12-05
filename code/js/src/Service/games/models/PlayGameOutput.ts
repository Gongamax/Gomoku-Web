import { SirenModel } from "../../media/siren/SirenModel"
import { GameOutputModel } from "./GameModelsUtil"
import { GameState } from "./GameModelsUtil"

interface GameRoundOutputModel {
    game : GameOutputModel;
    state : GameState;
}

export const PlayGameOutput = SirenModel<GameRoundOutputModel>