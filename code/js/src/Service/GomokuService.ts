import {HTTPService} from "./HttpService";

export class GomokuService extends HTTPService {

    /*
        Get the home page
        @return {Promise<HomeOutput>} The home page request
     */
    public async getHome(): Promise<HomeOutput> {
        return await this.get<HomeOutput>('/');
    }
}


type HomeOutput = string//SirenModel<HomeOutputModel>;