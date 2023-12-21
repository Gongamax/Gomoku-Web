import { Problem, problemMediaType } from './media/Problem';

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

  /**
   * Makes an API request and returns the response.
   * @param {string} path - The path of the API endpoint.
   * @param {string} method - The HTTP method to use for the request.
   * @param {string} [body] - The body of the request, if applicable.
   * @returns {Promise<T>} - The response from the API request.
   * @throws Will throw an error if the response is not ok.
   */
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
        const res = await response.json()
        throw res as Problem;
      } else throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return (await response.json()) as T;
  }

  /**
   * Makes a GET request to the specified path.
   * @param {string} path - The path of the API endpoint.
   * @returns {Promise<T>} - The response from the API request.
   */
  async function get<T>(path: string): Promise<T> {
    return makeAPIRequest<T>(path, 'GET', undefined);
  }

  /**
   * Makes a POST request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise<T>} - The response from the API request.
   */
  async function post<T>(path: string, body?: string): Promise<T> {
    return makeAPIRequest<T>(path, 'POST', body);
  }

  /**
   * Makes a PUT request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise<T>} - The response from the API request.
   */
  async function put<T>(path: string, body?: string): Promise<T> {
    return makeAPIRequest<T>(path, 'PUT', body);
  }

  /**
   * Makes a DELETE request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise<T>} - The response from the API request.
   */
  async function del<T>(path: string, body?: string): Promise<T> {
    return makeAPIRequest<T>(path, 'DELETE', body);
  }
}
