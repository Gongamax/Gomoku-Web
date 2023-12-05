import { SirenModel } from "../../media/siren/SirenModel"

interface UserGetByIdOutputModel {
    id : number;
    username : string;
    email : string;
}

export const GetUserOutput = SirenModel<UserGetByIdOutputModel>