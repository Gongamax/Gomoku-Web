import { HTTPService } from '../HttpService';
import { CancelMatchmakingOutput } from './models/CancelMatchmakingOutput';
import { GetGameOutput } from './models/GetGameOutput';
import { GetMatchmakingStatusOutput } from './models/GetMatchmakingStatusOutput';
import { GetAllGamesByUserOutput } from './models/GetUserGamesOutput';
import { GetVariantsOutput } from './models/GetVariantsOutput';
import { MatchmakingOutput } from './models/MatchmakingOutput';
import { PlayGameOutput } from './models/PlayGameOutput';
import { SurrenderGameOutput } from './models/SurrenderGameOutput';
import {linkRecipe} from "../../index";
import {HomeRecipeRelations} from "../home/HomeRecipeRelations";

const httpService = new HTTPService();

  export async function getGame(id: number): Promise<GetGameOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.GAME).href
        .replace('{gid}', id.toString());
    return await httpService.get<GetGameOutput>(path);
  }

  export async function playGame(gid: number, row: number, column: number): Promise<PlayGameOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.PLAY).href
        .replace('{gid}', gid.toString());
    return await httpService.post<PlayGameOutput>(path, JSON.stringify({ row, column }));
  }

  export async function matchmaking(variant: string): Promise<MatchmakingOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.MATCHMAKING).href;
    return await httpService.post<MatchmakingOutput>(path, JSON.stringify({ variant }));
  }

  export async function getMatchmakingStatus(mid: number): Promise<GetMatchmakingStatusOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.MATCHMAKING_STATUS).href
        .replace('{mid}', mid.toString());
    return await httpService.get<GetMatchmakingStatusOutput>(path);
  }

  export async function cancelMatchmaking(mid: number): Promise<CancelMatchmakingOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.EXIT_MATCHMAKING_QUEUE).href
        .replace('{mid}', mid.toString());
    return await httpService.delete<CancelMatchmakingOutput>(path);
  }

  export async function surrenderGame(gid: number): Promise<SurrenderGameOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.LEAVE).href
        .replace('{gid}', gid.toString());
    return await httpService.put<SurrenderGameOutput>(path);
  }

  export async function getAllGamesByUser(uid: number, page? : number): Promise<GetAllGamesByUserOutput> {
    let path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.GET_ALL_GAMES_BY_USER).href
        .replace('{uid}', uid.toString());
    if (page) {
      path = path.replace('1', page.toString());
    }
    return await httpService.get<GetAllGamesByUserOutput>(path);
  }

  export async function getVariantList(): Promise<GetVariantsOutput> {
    const path: string = (await linkRecipe)
        .find((recipe) => recipe.rel === HomeRecipeRelations.GET_ALL_VARIANTS).href;
    return await httpService.get<GetVariantsOutput>(path);
  }