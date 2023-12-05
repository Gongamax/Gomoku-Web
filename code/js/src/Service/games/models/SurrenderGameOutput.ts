import { SirenModel } from "../../media/siren/SirenModel"

interface SurrenderGameOutputModel {
    message : string;
}

export type SurrenderGameOutput = SirenModel<SurrenderGameOutputModel>