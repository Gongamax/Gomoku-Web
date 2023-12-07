import { HTTPService } from '../HttpService';
import { GetRankingOutput } from './models/GetRankingOutput';
import { GetUserHomeOutput } from './models/GetUserHomeOutput';
import { GetUserOutput } from './models/GetUserOutput';
import { LoginOutput } from './models/LoginOutput';
import { LogoutOutput } from './models/LogoutOutput';
import { RegisterOutput } from './models/RegisterOutput';
import { GetStatsOutput } from './models/GetStatsOutput';

export class UsersService extends HTTPService {
  public async register(username: string, email: string, password: string): Promise<RegisterOutput> {
    return await this.post<RegisterOutput>(
      '/users',
      JSON.stringify({
        username,
        email,
        password,
      })
    );
  }

  public async login(username: string, password: string): Promise<LoginOutput> {
    return await this.post<LoginOutput>(
      '/api/users/token',
      JSON.stringify({
        username,
        password,
      })
    );
  }

  public async logout(): Promise<LogoutOutput> {
    return await this.post<LogoutOutput>('/api/users/logout', JSON.stringify({}));
  }

  public async getUser(uid: number): Promise<GetUserOutput> {
    return await this.get<GetUserOutput>(`/api/users/${uid}`);
  }

  public async getAuthHome(): Promise<GetUserHomeOutput> {
    return await this.get<GetUserHomeOutput>('/api/me');
  }

  public async getRankingInfo(page: number): Promise<GetRankingOutput> {
    return await this.get<GetRankingOutput>(`/api/users/ranking?page=${page}`);
  }

  public async getStatsByUsername(username: string): Promise<GetStatsOutput> {
    return await this.get<GetStatsOutput>(`/api/stats/username?username=${username}`); //CHANGE THIS ON API TO PATH PARAM
  }
}
