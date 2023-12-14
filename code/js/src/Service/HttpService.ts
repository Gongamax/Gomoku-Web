import { problemMediaType } from './media/Problem';

/**
 * A service for making HTTP requests to the API.
 * It includes methods for GET, POST, PUT, and DELETE requests.
 */
export default function httpService() {
  return {
    get: get,
    post: post,
    put: put,
    del: del,
  };

  async function makeAPIRequest<T>(path: string, method: string, body?: string): Promise<T> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    const config: RequestInit = {
      method,
      credentials: 'include',
      headers,
      body: body,
    };

    const response = await fetch(path, config);

    if (!response.ok) {
      // Check if response is a Problem, not definitive solution
      if (response.headers.get('Content-Type')?.includes(problemMediaType)) {
        const problem = await response.json();
        throw new Error(problem.detail);
      } else throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return (await response.json()) as T;
  }

  async function get<T>(path: string): Promise<T> {
    return makeAPIRequest<T>(path, 'GET', undefined);
  }

  async function post<T>(path: string, body?: string): Promise<T> {
    return makeAPIRequest<T>(path, 'POST', body);
  }

  async function put<T>(path: string, body?: string): Promise<T> {
    return makeAPIRequest<T>(path, 'PUT', body);
  }

  async function del<T>(path: string, body?: string): Promise<T> {
    return makeAPIRequest<T>(path, 'DELETE', body);
  }
}

