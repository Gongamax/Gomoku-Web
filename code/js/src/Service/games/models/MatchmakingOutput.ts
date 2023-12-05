import { SirenModel } from "../../media/siren/SirenModel"

interface  GameMatchmakingOutputModel {
    message : string;
    idType : string;
    id : number;
}

export const MatchmakingOutput = SirenModel<GameMatchmakingOutputModel>