import { SirenModel } from "../../media/siren/SirenModel"

interface UserHomeOutputModel {
    uid : number;
    username : string;
    message : string;
}

export type GetUserHomeOutput = SirenModel<UserHomeOutputModel>
