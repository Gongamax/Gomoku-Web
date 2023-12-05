import { SirenModel } from "../../media/siren/SirenModel"
import { GameOutputModel } from "./GameModelsUtil"

interface GameGetByIdOutputModel {
    game : GameOutputModel;
}

export const GetGameOutput = SirenModel<GameGetByIdOutputModel>