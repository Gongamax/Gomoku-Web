import { HTTPService } from '../HttpService';
import { CancelMatchmakingOutput } from './models/CancelMatchmakingOutput';
import { GetGameOutput } from './models/GetGameOutput';
import { GetMatchmakingStatusOutput } from './models/GetMatchmakingStatusOutput';
import { GetAllGamesByUserOutput } from './models/GetUserGamesOutput';
import { GetVariantsOutput } from './models/GetVariantsOutput';
import { MatchmakingOutput } from './models/MatchmakingOutput';
import { PlayGameOutput } from './models/PlayGameOutput';
import { SurrenderGameOutput } from './models/SurrenderGameOutput';

export class GamesServices extends HTTPService {

  public async getGame(id: number): Promise<GetGameOutput> {
    return await this.get<GetGameOutput>(`/api/games/${id}`);
  }

  public async playGame(gid: number, row: number, col: number): Promise<PlayGameOutput> {
    return await this.post<PlayGameOutput>(`/api/games/${gid}/play`, JSON.stringify({ row, col }));
  }

  public async matchmaking(variant: string): Promise<MatchmakingOutput> {
    return await this.post<MatchmakingOutput>(`/api/games/matchmaking`, JSON.stringify({ variant }));
  }

  public async getMatchmakingStatus(mid: number): Promise<GetMatchmakingStatusOutput> {
    return await this.get<GetMatchmakingStatusOutput>(`/api/games/matchmaking/${mid}/status`);
  }

  public async cancelMatchmaking(mid: number): Promise<CancelMatchmakingOutput> {
    return await this.delete<CancelMatchmakingOutput>(`/api/games/matchmaking/${mid}/exit`);
  }

  public async surrenderGame(gid: number): Promise<SurrenderGameOutput> {
    return await this.put<SurrenderGameOutput>(`/api/games/${gid}/leave`);
  }

  public async getAllGamesByUser(uid: number): Promise<GetAllGamesByUserOutput> {
    return await this.get<GetAllGamesByUserOutput>(`/api/games/user/${uid}`);
  }

  public async getVariantList(): Promise<GetVariantsOutput> {
    return await this.get<GetVariantsOutput>(`/api/games/variants`);
  }
}
