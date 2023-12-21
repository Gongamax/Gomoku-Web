import httpServiceInit from "./HttpService";
import {SirenModel} from "./media/siren/SirenModel";
import {HomeOutputModel} from './home/HomeOutputModel';

const httpService = httpServiceInit();
type HomeOutput = SirenModel<HomeOutputModel>;
const HOME_URI = '/api/';

/**
    Get the home page
    @return {Promise<HomeOutput>} The home page request
*/
export async function getHome(): Promise<HomeOutput> {
    return await httpService.get<HomeOutput>(HOME_URI);
}
