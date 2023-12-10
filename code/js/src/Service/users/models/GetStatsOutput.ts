import { SirenModel } from "../../media/siren/SirenModel"

export interface UserStatsOutputModel {
    uid : number;
    username : string;
    gamesPlayed : number;
    wins : number;
    losses : number;
    rank : number;
    points : number;
}

export type  GetStatsOutput = SirenModel<UserStatsOutputModel>