import { SirenModel } from "../../media/siren/SirenModel"
import { GameOutputModel } from "./GameModelsUtil"

export interface GamesOfUser {
    games : GameOutputModel[];
}

interface GameGetAllByUserOutputModel {
    uid : number;
    page : number;
    pageSize : number;
}

export type GetAllGamesByUserOutput = SirenModel<GameGetAllByUserOutputModel>