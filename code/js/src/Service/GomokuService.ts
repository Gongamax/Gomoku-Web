import {HTTPService} from "./HttpService";
import {SirenModel} from "./media/siren/SirenModel";
import { HomeOutputModel } from './home/HomeOutputModel';


const httpService: HTTPService = new HTTPService();
type HomeOutput = SirenModel<HomeOutputModel>;

/**
    Get the home page
    @return {Promise<HomeOutput>} The home page request
*/
export async function getHome(): Promise<HomeOutput> {
    return await httpService.get<HomeOutput>('/');
}
