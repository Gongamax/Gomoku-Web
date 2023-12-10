import { HTTPService } from '../HttpService';
import { GetRankingOutput } from './models/GetRankingOutput';
import { GetUserHomeOutput } from './models/GetUserHomeOutput';
import { GetUserOutput } from './models/GetUserOutput';
import { LoginOutput } from './models/LoginOutput';
import { LogoutOutput } from './models/LogoutOutput';
import { RegisterOutput } from './models/RegisterOutput';
import { GetStatsOutput } from './models/GetStatsOutput';
import {linkRecipe} from "../../index";
import { HomeRecipeRelations } from '../home/HomeRecipeRelations';

const httpService = new HTTPService();

export async function register(username: string, email: string, password: string): Promise<RegisterOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.REGISTER).href;
    return await httpService.post<RegisterOutput>(
        path,
        JSON.stringify({
            username,
            email,
            password,
        }),
    );
}

export async function login(username: string, password: string): Promise<LoginOutput> {
    const path: string = (await linkRecipe)
            .find((recipe) => recipe.rel === HomeRecipeRelations.LOGIN).href;
    return await httpService.post<LoginOutput>(
        path,
        JSON.stringify({
            username,
            password,
        }),
    );
}

export async function logout(): Promise<LogoutOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.LOGOUT).href;
    return await httpService.post<LogoutOutput>(path);
}

export async function getUser(id: number): Promise<GetUserOutput> {
  const path: string = (await linkRecipe)
      .find((recipe) => recipe.rel === HomeRecipeRelations.USER).href
      .replace('{uid}', id.toString());
  return await httpService.get<GetUserOutput>(path);
}

export async function getUserHome(): Promise<GetUserHomeOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.AUTH_HOME).href;
    return await httpService.get<GetUserHomeOutput>(path);
}

export async function getRanking(page?: number): Promise<GetRankingOutput> {
    let path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.RANKING_INFO).href;
    if (page) {
        path = path.replace('1', page.toString());
    }
    return await httpService.get<GetRankingOutput>(path);
}

export async function getStatsByUsername(username: string): Promise<GetStatsOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.USER_STATS_BY_USERNAME).href
        .replace('{name}', username);
    return await httpService.get<GetStatsOutput>(path)
}

export async function getStatsById(id: number): Promise<GetStatsOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.USER_STATS).href
        .replace('{uid}', id.toString());
  return await httpService.get<GetStatsOutput>(path);
}

