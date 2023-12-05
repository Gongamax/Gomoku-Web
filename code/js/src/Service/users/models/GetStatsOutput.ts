import { SirenModel } from "../../media/siren/SirenModel"

interface UserStatsOutputModel {
    id : number;
    username : string;
    gamesPlayed : number;
    wins : number;
    losses : number;
    rank : number;
    points : number;
}

export type  GetStatsOutput = SirenModel<UserStatsOutputModel>