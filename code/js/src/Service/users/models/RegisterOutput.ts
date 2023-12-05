import { SirenModel } from "../../media/siren/SirenModel"

interface UserCreateOutputModel {
    uid : number;
}

export const RegisterOutput = SirenModel<UserCreateOutputModel>