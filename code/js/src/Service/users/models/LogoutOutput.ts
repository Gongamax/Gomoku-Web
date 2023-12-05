import { SirenModel } from "../../media/siren/SirenModel"

interface UserTokenRemoveOutputModel {
    message : string;
}

export const LogoutOutput = SirenModel<UserTokenRemoveOutputModel>