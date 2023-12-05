import { SirenModel } from "../../media/siren/SirenModel"
import { GameOutputModel } from "./GameModelsUtil"

interface GameGetAllByUserOutputModel {
    games : GameOutputModel[];
}

export const GetAllGamesByUserOutput = SirenModel<GameGetAllByUserOutputModel>