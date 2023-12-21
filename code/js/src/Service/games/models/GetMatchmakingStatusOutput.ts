import { SirenModel } from "../../media/siren/SirenModel"

interface GetMatchmakingStatusOutputModel {
    mid : number;
    uid : number;
    gid : number;
    state : string;
    variant : string;
    created : string;
    pollingTimeOut: number
}

export type GetMatchmakingStatusOutput = SirenModel<GetMatchmakingStatusOutputModel>