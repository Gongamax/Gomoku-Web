import { SirenModel } from "../../media/siren/SirenModel"

interface UserCreateOutputModel {
    uid : number;
}

export type RegisterOutput = SirenModel<UserCreateOutputModel>