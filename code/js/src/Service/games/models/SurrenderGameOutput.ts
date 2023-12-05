import { SirenModel } from "../../media/siren/SirenModel"

interface SurrenderGameOutputModel {
    message : string;
}

export const SurrenderGameOutput = SirenModel<SurrenderGameOutputModel>