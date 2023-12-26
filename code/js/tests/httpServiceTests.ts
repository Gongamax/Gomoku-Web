import httpService from '../src/Service/HttpService';
import { Problem } from '../src/Service/media/Problem';

describe('HttpService', () => {
  const mockFetch = jest.fn();
  global.fetch = mockFetch;

  beforeEach(() => {
    mockFetch.mockClear();
  });

  it('should make a successful GET request', async () => {
    const mockResponse = { data: 'test' };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    });

    const result = await httpService().get('/test');
    expect(result).toEqual(mockResponse);
    expect(mockFetch).toHaveBeenCalledWith('/test', expect.objectContaining({ method: 'GET' }));
  });

  it('should make a successful POST request', async () => {
    const mockResponse = { data: 'test' };
    const mockBody = JSON.stringify({ key: 'value' });
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    });

    const result = await httpService().post('/test', mockBody);
    expect(result).toEqual(mockResponse);
    expect(mockFetch).toHaveBeenCalledWith('/test', expect.objectContaining({ method: 'POST', body: mockBody }));
  });

  it('should throw an error for a failed request', async () => {
    const mockProblem: Problem = { typeUri: 'about:blank', title: 'Test Error', status: 400, detail: 'Test Detail' };
    mockFetch.mockResolvedValueOnce({
      ok: false,
      headers: { get: () => 'application/problem+json' },
      json: () => Promise.resolve(mockProblem),
    });

    await expect(httpService().get('/test')).rejects.toEqual(mockProblem);
  });

  it('should throw a generic error for a failed request with non-problem response', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      statusText: 'Internal Server Error',
    });

    await expect(httpService().get('/test')).rejects.toThrow('HTTP error! Status: 500');
  });
});