import { SirenModel } from "../../media/siren/SirenModel"

interface UserGetByIdOutputModel {
    uid : number;
    username : string;
    email : string;
}

export type GetUserOutput = SirenModel<UserGetByIdOutputModel>