export class HTTPService {
  private async makeAPIRequest<T>(path: string, method: string, body?: string): Promise<T> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    const config: RequestInit = {
      method,
      headers,
      body: body,
    };

    console.log(`Making ${method} request to ${path}`);
    console.log(`Body: ${body}`);

    const response = await fetch(path, config);

    if (!response.ok) {
      // Check if response is a Problem, not definitive solution
      if (response.headers.get('Content-Type')?.includes('application/problem+json')) {
        const problem = await response.json();
        throw new Error(problem.detail);
      } else throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return (await response.json()) as T;
  }

  public async get<T>(path: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'GET', undefined);
  }

  public async post<T>(path: string, body?: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'POST', body);
  }

  public async put<T>(path: string, body?: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'PUT', body);
  }

  public async delete<T>(path: string, body?: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'DELETE', body);
  }
}
