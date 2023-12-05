import { SirenModel } from "../../media/siren/SirenModel"

interface UserTokenRemoveOutputModel {
    message : string;
}

export type LogoutOutput = SirenModel<UserTokenRemoveOutputModel>