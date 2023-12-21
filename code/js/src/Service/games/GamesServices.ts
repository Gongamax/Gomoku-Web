import { CancelMatchmakingOutput } from './models/CancelMatchmakingOutput';
import { GetGameOutput } from './models/GetGameOutput';
import { GetMatchmakingStatusOutput } from './models/GetMatchmakingStatusOutput';
import { GetAllGamesByUserOutput } from './models/GetUserGamesOutput';
import { GetVariantsOutput } from './models/GetVariantsOutput';
import { MatchmakingOutput } from './models/MatchmakingOutput';
import { PlayGameOutput } from './models/PlayGameOutput';
import { SurrenderGameOutput } from './models/SurrenderGameOutput';
import { linkRecipe } from '../../index';
import { HomeRecipeRelations } from '../home/HomeRecipeRelations';
import httpServiceInit from '../HttpService';

const httpService = httpServiceInit();

/**
 * Fetches the game data from the server.
 *
 * @param {number} id - The ID of the game.
 * @returns {Promise<GetGameOutput>} The game data.
 */
export async function getGame(id: number): Promise<GetGameOutput> {
  const path: string = (await linkRecipe)
    .find(recipe => recipe.rel === HomeRecipeRelations.GAME)
    .href.replace('{gid}', id.toString());
  return await httpService.get<GetGameOutput>(path);
}

/**
 * Sends a request to the server to make a move in the game.
 *
 * @param {number} gid - The ID of the game.
 * @param {number} row - The row index of the move.
 * @param {number} column - The column index of the move.
 * @returns {Promise<PlayGameOutput>} The updated game data.
 */
export async function playGame(gid: number, row: number, column: number): Promise<PlayGameOutput> {
  const path: string = (await linkRecipe)
    .find(recipe => recipe.rel === HomeRecipeRelations.PLAY)
    .href.replace('{gid}', gid.toString());
  return await httpService.post<PlayGameOutput>(path, JSON.stringify({ row, column }));
}

/**
 * Sends a request to the server to start matchmaking.
 *
 * @param {string} variant - The variant of the game.
 * @returns {Promise<MatchmakingOutput>} The matchmaking data.
 */
export async function matchmaking(variant: string): Promise<MatchmakingOutput> {
  const path: string = (await linkRecipe).find(recipe => recipe.rel === HomeRecipeRelations.MATCHMAKING).href;
  return await httpService.post<MatchmakingOutput>(path, JSON.stringify({ variant }));
}

/**
 * Fetches the matchmaking status from the server.
 *
 * @param {number} mid - The ID of the matchmaking.
 * @returns {Promise<GetMatchmakingStatusOutput>} The matchmaking status.
 */
export async function getMatchmakingStatus(mid: number): Promise<GetMatchmakingStatusOutput> {
  const path: string = (await linkRecipe)
    .find(recipe => recipe.rel === HomeRecipeRelations.MATCHMAKING_STATUS)
    .href.replace('{mid}', mid.toString());
  return await httpService.get<GetMatchmakingStatusOutput>(path);
}

/**
 * Sends a request to the server to cancel matchmaking.
 *
 * @param {number} mid - The ID of the matchmaking.
 * @returns {Promise<CancelMatchmakingOutput>} The cancellation data.
 */
export async function cancelMatchmaking(mid: number): Promise<CancelMatchmakingOutput> {
  const path: string = (await linkRecipe)
    .find(recipe => recipe.rel === HomeRecipeRelations.EXIT_MATCHMAKING_QUEUE)
    .href.replace('{mid}', mid.toString());
  return await httpService.del<CancelMatchmakingOutput>(path);
}

/**
 * Sends a request to the server to surrender from the game.
 *
 * @param {number} gid - The ID of the game.
 * @returns {Promise<SurrenderGameOutput>} The surrender data.
 */
export async function surrenderGame(gid: number): Promise<SurrenderGameOutput> {
  const path: string = (await linkRecipe)
    .find(recipe => recipe.rel === HomeRecipeRelations.LEAVE)
    .href.replace('{gid}', gid.toString());
  return await httpService.put<SurrenderGameOutput>(path);
}

/**
 * Fetches all games of a user from the server.
 *
 * @param {number} uid - The ID of the user.
 * @param {number} [page] - The page number.
 * @returns {Promise<GetAllGamesByUserOutput>} The user's games.
 */
export async function getAllGamesByUser(uid: number, page?: number): Promise<GetAllGamesByUserOutput> {
  let path: string = (await linkRecipe)
    .find(recipe => recipe.rel === HomeRecipeRelations.GET_ALL_GAMES_BY_USER)
    .href.replace('{uid}', uid.toString());
  if (page) {
    path = path.replace('1', page.toString());
  }
  return await httpService.get<GetAllGamesByUserOutput>(path);
}

/**
 * Fetches the list of game variants from the server.
 *
 * @returns {Promise<GetVariantsOutput>} The list of game variants.
 */
export async function getVariantList(): Promise<GetVariantsOutput> {
  const path: string = (await linkRecipe).find(recipe => recipe.rel === HomeRecipeRelations.GET_ALL_VARIANTS).href;
  return await httpService.get<GetVariantsOutput>(path);
}
