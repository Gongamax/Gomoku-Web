import { test, expect, Browser, BrowserContext, Page, chromium } from '@playwright/test';

// Define a context to store the user credentials
let userCredentials1 = { username: '', password: '' };
let userCredentials2 = { username: '', password: '' };
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
  await Promise.all([
    page.waitForResponse(response => response.url().includes('/api/users') && response.status() === 201),
    registerButton.click(),
  ]);

  // Store the user credentials
  userCredentials1 = { username, password };

  // User 2
  // Navigate to the registration page
  await page.goto('http://localhost:8000/register');

  // Fill in the registration form
  const emailInput2 = page.getByLabel('Email');
  const usernameInput2 = page.getByLabel('Username');
  const passwordInput2 = page.getByLabel('Password', { exact: true });
  const confirmPasswordInput2 = page.getByLabel('Confirm Password', { exact: true });
  const registerButton2 = page.getByRole('button');

  // Generate unique username and password
  const email2 = `user${Math.floor(Math.random() * 10000)}@example.com`;
  const username2 = `user${Math.floor(Math.random() * 10000)}`;
  const password2 = `Password${Math.floor(Math.random() * 10000)}`;

  await emailInput2.fill(email2);
  await usernameInput2.fill(username2);
  await passwordInput2.fill(password2);
  await confirmPasswordInput2.fill(password2);
  await Promise.all([
    page.waitForResponse(response => response.url().includes('/api/users') && response.status() === 201),
    registerButton2.click(),
  ]);

  // Store the user credentials
  userCredentials2 = { username: username2, password: password2 };
});

test.afterAll(async () => {
  // Close the browser context after all tests
  await context.close();
  await browser.close();
  // Clear the user credentials after all tests
  userCredentials1 = { username: '', password: '' };
  userCredentials2 = { username: '', password: '' };
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
  await loginUser(page1, userCredentials1.username, userCredentials1.password);
  await loginUser(page2, userCredentials2.username, userCredentials2.password);

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
  await page1.waitForSelector('text="Turn: Black"');
  await expect(page1.getByText('Turn: Black')).toBeVisible();

  // In browser2, surrender the game
  const surrenderButton = page1.getByRole('button', { name: 'Surrender' });
  await surrenderButton.click();

  // then in browser1, the game is over
  const gameResult = page2.getByText('Game Over');
  await expect(gameResult).toBeVisible();

  // Close the browsers
  await browser1.close();
  await browser2.close();
});
