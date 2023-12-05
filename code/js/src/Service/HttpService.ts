import { SirenModel } from './media/siren/SirenModel';

export class HTTPService {
  private apiEndpoint: string;

  constructor(apiEndpoint: string) {
    this.apiEndpoint = apiEndpoint;
  }

  private async makeAPIRequest<T>(path: string, method: string, body?: T, token?: string): Promise<SirenModel<T>> {
    const url = `${this.apiEndpoint}/${path}`;
    const headers: Record<string, string> = {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config: RequestInit = {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    };

    const response = await fetch(url, config);

    if (!response.ok) {
      // Handle error if needed
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const responseData = (await response.json()) as SirenModel<T>;
    return responseData;
  }

  public async get<T>(path: string, token?: string): Promise<SirenModel<T>> {
    return this.makeAPIRequest<T>(path, 'GET', undefined, token);
  }

  public async post<T>(path: string, body: T, token?: string): Promise<SirenModel<T>> {
    return this.makeAPIRequest<T>(path, 'POST', body, token);
  }

  public async put<T>(path: string, body: T, token?: string): Promise<SirenModel<T>> {
    return this.makeAPIRequest<T>(path, 'PUT', body, token);
  }

  public async delete<T>(path: string, body: T, token?: string): Promise<SirenModel<T>> {
    return this.makeAPIRequest<T>(path, 'DELETE', body, token);
  }
}
