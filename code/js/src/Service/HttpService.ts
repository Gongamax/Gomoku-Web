/**
 * HTTPService is a class that provides methods to make HTTP requests.
 * It includes methods for GET, POST, PUT, and DELETE requests.
 */
export class HTTPService {
  /**
   * Makes an API request and returns the response.
   * @param {string} path - The path of the API endpoint.
   * @param {string} method - The HTTP method to use for the request.
   * @param {string} [body] - The body of the request, if applicable.
   * @returns {Promise<T>} - The response from the API request.
   * @throws Will throw an error if the response is not ok.
   */
  private async makeAPIRequest<T>(path: string, method: string, body?: string): Promise<T> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    const config: RequestInit = {
      method,
      credentials: 'include',
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

  /**
   * Makes a GET request to the specified path.
   * @param {string} path - The path of the API endpoint.
   * @returns {Promise<T>} - The response from the API request.
   */
  public async get<T>(path: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'GET', undefined);
  }

  /**
   * Makes a POST request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise<T>} - The response from the API request.
   */
  public async post<T>(path: string, body?: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'POST', body);
  }

  /**
   * Makes a PUT request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise<T>} - The response from the API request.
   */
  public async put<T>(path: string, body?: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'PUT', body);
  }

  /**
   * Makes a DELETE request to the specified path with the provided body.
   * @param {string} path - The path of the API endpoint.
   * @param {string} [body] - The body of the request.
   * @returns {Promise<T>} - The response from the API request.
   */
  public async delete<T>(path: string, body?: string): Promise<T> {
    return this.makeAPIRequest<T>(path, 'DELETE', body);
  }
}