import { SirenModel } from "../../media/siren/SirenModel"

interface UserTokenCreateOutputModel {
    token : string;
}

export const LoginOutput = SirenModel<UserTokenCreateOutputModel>