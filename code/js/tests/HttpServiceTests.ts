// import { rest } from 'msw';
// import { setupServer } from 'msw/node';
// import { HTTPService } from '../src/Service/HttpService';

// const server = setupServer(
//   rest.get('/api/test', (req : unknown, res : unknown, ctx) => {
//     return res(ctx.json({ message: 'Hello, world!' }));
//   })
// );

// beforeAll(() => server.listen());
// afterEach(() => server.resetHandlers());
// afterAll(() => server.close());

// describe('HTTPService', () => {
//   it('should make a GET request to the API', async () => {
//     const http = new HTTPService('/api');
//     const response = await http.get<{ message: string }>('/test');
//     expect(response.message).toBe('Hello, world!');
//   });
// });