import { SirenModel } from "../../media/siren/SirenModel"

interface CancelMatchmakingOutputModel {
    message : string;
}

export const CancelMatchmakingOutput = SirenModel<CancelMatchmakingOutputModel>