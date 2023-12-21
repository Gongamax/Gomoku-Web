import { test, expect, Browser, BrowserContext, Page, chromium } from '@playwright/test';

// Define a context to store the user credentials
let userCredentials = { username: '', password: '' };
let browser: Browser;
let context: BrowserContext;
let page: Page;

test.beforeAll(async () => {
  // Create a new browser context and page
  browser = await chromium.launch();
  context = await browser.newContext();
  page = await context.newPage();

  // Navigate to the registration page
  await page.goto('http://localhost:8000/register');

  // Fill in the registration form
  const emailInput = page.getByLabel('Email');
  const usernameInput = page.getByLabel('Username');
  const passwordInput = page.getByLabel('Password', { exact: true });
  const confirmPasswordInput = page.getByLabel('Confirm Password', { exact: true });
  const registerButton = page.getByRole('button');

  // Generate unique username and password
  const email = `user${Math.floor(Math.random() * 10000)}@example.com`;
  const username = `user${Math.floor(Math.random() * 10000)}`;
  const password = `Password${Math.floor(Math.random() * 10000)}`;

  await emailInput.fill(email);
  await usernameInput.fill(username);
  await passwordInput.fill(password);
  await confirmPasswordInput.fill(password);
  await registerButton.click();

  // Store the user credentials
  userCredentials = { username, password };
});

test.afterAll(async () => {
  // Close the browser context after all tests
  await context.close();
  await browser.close();
  // Clear the user credentials after all tests
  userCredentials = { username: '', password: '' };
});

test('two users can register, log in, find a game, and play a game', async () => {
  // Launch two browsers
  const browser1 = await chromium.launch();
  const browser2 = await chromium.launch();

  // Create a new context and a new page in each browser
  const context1 = await browser1.newContext();
  const page1 = await context1.newPage();
  const context2 = await browser2.newContext();
  const page2 = await context2.newPage();

  // Define a helper function to log in a user
  const loginUser = async (page: Page, username: string, password: string) => {
    await page.goto('http://localhost:8000/login');
    await page.fill('input[name="username"]', username);
    await page.fill('input[name="password"]', password);
    await page.click('button[type="submit"]');
  };

  // Log in the user in each browser
  await loginUser(page1, userCredentials.username, userCredentials.password);
  await loginUser(page2, userCredentials.username, userCredentials.password);

  // Navigate to the lobby page in each browser and search for a game
  await page1.click('a[href="/lobby"]');
  await page1.click('text=STANDARD');
  const findGameButton1 = page1.getByRole('button', { name: 'Find Game' });
  await findGameButton1.click();
  await page2.click('a[href="/lobby"]');
  await page2.click('text=STANDARD');
  const findGameButton2 = page2.getByRole('button', { name: 'Find Game' });
  await findGameButton2.click();

  // In browser1, wait for the game to start
  await expect(page.getByText('Turn: BLACK')).toBeVisible();

  // In browser1, game starts
  const gameTurn = page1.getByText('Turn: BLACK');
  await expect(gameTurn).toBeVisible();

  // In browser1, surrender the game
  const surrenderButton = page1.getByRole('button', { name: 'Surrender' });
  await surrenderButton.click();

  // then in browser2, the game is over
  const gameResult = page2.getByText('Game Over');
  await expect(gameResult).toBeVisible();

  // Close the browsers
  await browser1.close();
  await browser2.close();
});