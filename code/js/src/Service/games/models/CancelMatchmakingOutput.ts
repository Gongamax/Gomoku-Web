import { SirenModel } from "../../media/siren/SirenModel"

interface CancelMatchmakingOutputModel {
    message : string;
}

export type CancelMatchmakingOutput = SirenModel<CancelMatchmakingOutputModel>