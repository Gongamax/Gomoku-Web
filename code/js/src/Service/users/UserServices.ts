import { HTTPService } from '../HttpService';
import { GetRankingOutput } from './models/GetRankingOutput';
import { GetUserHomeOutput } from './models/GetUserHomeOutput';
import { GetUserOutput } from './models/GetUserOutput';
import { LoginOutput } from './models/LoginOutput';
import { LogoutOutput } from './models/LogoutOutput';
import { RegisterOutput } from './models/RegisterOutput';
import { GetStatsOutput } from './models/GetStatsOutput';

const httpService = new HTTPService();

export async function register(username: string, email: string, password: string): Promise<RegisterOutput> {
  return await httpService.post<RegisterOutput>(
    '/api/users',
    JSON.stringify({
      username,
      email,
      password,
    }),
  );
}

export async function login(username: string, password: string): Promise<LoginOutput> {
  return await httpService.post<LoginOutput>(
    '/api/users/token',
    JSON.stringify({
      username,
      password,
    }),
  );
}

export async function logout(): Promise<LogoutOutput> {
  return await httpService.post<LogoutOutput>('/api/logout');
}

export async function getUser(id: number): Promise<GetUserOutput> {
  return await httpService.get<GetUserOutput>(`/api/users/${id}`);
}

export async function getUserHome(): Promise<GetUserHomeOutput> {
  return await httpService.get<GetUserHomeOutput>(`/api/me`);
}

export async function getRanking(page:number): Promise<GetRankingOutput> {
  return await httpService.get<GetRankingOutput>(`/api/users/ranking?page=${page}`);
}

export async function getStatsByUsername(username: string): Promise<GetStatsOutput> {
  return await httpService.get<GetStatsOutput>(`/api/stats/username?username=${username}`); //CHANGE THIS ON API TO PATH PARAM
}

