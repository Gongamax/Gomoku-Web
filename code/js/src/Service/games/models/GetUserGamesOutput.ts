import { type } from "os";
import { SirenModel } from "../../media/siren/SirenModel"
import { GameOutputModel } from "./GameModelsUtil"

interface GameGetAllByUserOutputModel {
    games : GameOutputModel[];
}

export type GetAllGamesByUserOutput = SirenModel<GameGetAllByUserOutputModel>