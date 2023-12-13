import {SirenModel} from "../../media/siren/SirenModel"
import {GameOutputModel} from "./GameModelsUtil"

interface GameRoundOutputModel {
    game : GameOutputModel;
}

export type PlayGameOutput = SirenModel<GameRoundOutputModel>