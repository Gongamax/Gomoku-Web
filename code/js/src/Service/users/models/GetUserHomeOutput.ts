import { SirenModel } from "../../media/siren/SirenModel"

interface UserHomeOutputModel {
    id : number;
    username : string;
}

export const GetUserHomeOutput = SirenModel<UserHomeOutputModel>
