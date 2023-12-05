import { SirenModel } from "../../media/siren/SirenModel"

interface UserTokenCreateOutputModel {
    token : string;
}

export type LoginOutput = SirenModel<UserTokenCreateOutputModel>