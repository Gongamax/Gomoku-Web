import { GetRankingOutput } from './models/GetRankingOutput';
import { GetUserHomeOutput } from './models/GetUserHomeOutput';
import { GetUserOutput } from './models/GetUserOutput';
import { LoginOutput } from './models/LoginOutput';
import { LogoutOutput } from './models/LogoutOutput';
import { RegisterOutput } from './models/RegisterOutput';
import { GetStatsOutput } from './models/GetStatsOutput';
import {linkRecipe} from "../../index";
import { HomeRecipeRelations } from '../home/HomeRecipeRelations';
import httpServiceInit from '../HttpService';
const httpService = httpServiceInit();

/**
 * Sends a request to the server to register a new user.
 *
 * @param {string} username - The username of the new user.
 * @param {string} email - The email of the new user.
 * @param {string} password - The password of the new user.
 * @returns {Promise<RegisterOutput>} The server's response to the registration request.
 */
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

/**
 * Sends a request to the server to log in a user.
 *
 * @param {string} username - The username of the user.
 * @param {string} password - The password of the user.
 * @returns {Promise<LoginOutput>} The server's response to the login request.
 */
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

/**
 * Sends a request to the server to log out the current user.
 *
 * @returns {Promise<LogoutOutput>} The server's response to the logout request.
 */
export async function logout(): Promise<LogoutOutput> {
  const path: string = (await linkRecipe)
    .find((recipe) => recipe.rel === HomeRecipeRelations.LOGOUT).href;
  return await httpService.post<LogoutOutput>(path);
}

/**
 * Fetches the data of a user from the server.
 *
 * @param {number} id - The ID of the user.
 * @returns {Promise<GetUserOutput>} The user's data.
 */
export async function getUser(id: number): Promise<GetUserOutput> {
  const path: string = (await linkRecipe)
    .find((recipe) => recipe.rel === HomeRecipeRelations.USER).href
    .replace('{uid}', id.toString());
  return await httpService.get<GetUserOutput>(path);
}

/**
 * Fetches the home data of the current user from the server.
 *
 * @returns {Promise<GetUserHomeOutput>} The home data of the current user.
 */
export async function getUserHome(): Promise<GetUserHomeOutput> {
  const path: string = (await linkRecipe)
    .find((recipe) => recipe.rel === HomeRecipeRelations.AUTH_HOME).href;
  return await httpService.get<GetUserHomeOutput>(path);
}

/**
 * Fetches the ranking data from the server.
 *
 * @param {number} [page] - The page number.
 * @returns {Promise<GetRankingOutput>} The ranking data.
 */
export async function getRanking(page?: number): Promise<GetRankingOutput> {
  let path: string = (await linkRecipe)
    .find((recipe) => recipe.rel === HomeRecipeRelations.RANKING_INFO).href;
  if (page) {
    path = path.replace('1', page.toString());
  }
  return await httpService.get<GetRankingOutput>(path);
}

/**
 * Fetches the stats of a user by username from the server.
 *
 * @param {string} username - The username of the user.
 * @returns {Promise<GetStatsOutput>} The user's stats.
 */
export async function getStatsByUsername(username: string): Promise<GetStatsOutput> {
  const path: string = (await linkRecipe)
    .find((recipe) => recipe.rel === HomeRecipeRelations.USER_STATS_BY_USERNAME).href
    .replace('{name}', username);
  return await httpService.get<GetStatsOutput>(path)
}

/**
 * Fetches the stats of a user by ID from the server.
 *
 * @param {number} id - The ID of the user.
 * @returns {Promise<GetStatsOutput>} The user's stats.
 */
export async function getStatsById(id: number): Promise<GetStatsOutput> {
  const path: string = (await linkRecipe)
    .find((recipe) => recipe.rel === HomeRecipeRelations.USER_STATS).href
    .replace('{uid}', id.toString());
  return await httpService.get<GetStatsOutput>(path);
}

