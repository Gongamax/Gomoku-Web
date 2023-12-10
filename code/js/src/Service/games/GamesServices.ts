import { HTTPService } from '../HttpService';
import { CancelMatchmakingOutput } from './models/CancelMatchmakingOutput';
import { GetGameOutput } from './models/GetGameOutput';
import { GetMatchmakingStatusOutput } from './models/GetMatchmakingStatusOutput';
import { GetAllGamesByUserOutput } from './models/GetUserGamesOutput';
import { GetVariantsOutput } from './models/GetVariantsOutput';
import { MatchmakingOutput } from './models/MatchmakingOutput';
import { PlayGameOutput } from './models/PlayGameOutput';
import { SurrenderGameOutput } from './models/SurrenderGameOutput';

const httpService = new HTTPService();

  export async function getGame(id: number): Promise<GetGameOutput> {
    return await httpService.get<GetGameOutput>(`/api/games/${id}`);
  }

  export async function playGame(gid: number, row: number, col: number): Promise<PlayGameOutput> {
    return await httpService.post<PlayGameOutput>(`/api/games/${gid}/play`, JSON.stringify({ row, col }));
  }

  export async function matchmaking(variant: string): Promise<MatchmakingOutput> {
    return await httpService.post<MatchmakingOutput>(`/api/games/matchmaking`, JSON.stringify({ variant }));
  }

  export async function getMatchmakingStatus(mid: number): Promise<GetMatchmakingStatusOutput> {
    return await httpService.get<GetMatchmakingStatusOutput>(`/api/games/matchmaking/${mid}/status`);
  }

  export async function cancelMatchmaking(mid: number): Promise<CancelMatchmakingOutput> {
    return await httpService.delete<CancelMatchmakingOutput>(`/api/games/matchmaking/${mid}/exit`);
  }

  export async function surrenderGame(gid: number): Promise<SurrenderGameOutput> {
    return await httpService.put<SurrenderGameOutput>(`/api/games/${gid}/leave`);
  }

  export async function getAllGamesByUser(uid: number, page? : number): Promise<GetAllGamesByUserOutput> {
    return await httpService.get<GetAllGamesByUserOutput>(`/api/games/user/${uid}?page=${page}`);
  }

  export async function getVariantList(): Promise<GetVariantsOutput> {
    return await httpService.get<GetVariantsOutput>(`/api/games/variants`);
  }

