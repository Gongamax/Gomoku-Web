import { SirenModel } from "../../media/siren/SirenModel"

interface GetMatchmakingStatusOutputModel {
    id : number;
    userId : number;
    gameId : number;
    state : string;
    variant : string;
    created : string;
}

export type GetMatchmakingStatusOutput = SirenModel<GetMatchmakingStatusOutputModel>