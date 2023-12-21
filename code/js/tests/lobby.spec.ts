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

test('user can log in, navigate to lobby, and select a variant', async ({ page }) => {
  // when: navigating to the login page
  await page.goto('http://localhost:8000/login');

  // then: fill in the login form with valid credentials and submit
  await page.fill('input[name="username"]', userCredentials.username);
  await page.fill('input[name="password"]', userCredentials.password);
  await page.click('button[type="submit"]');

  // when: navigating to the lobby page
  await page.click('a[href="/lobby"]');

  // then: the lobby page appears
  const lobbyTitle = page.getByRole('heading', { name: 'Lobby', exact: true });
  await expect(lobbyTitle).toBeVisible();

  // when: selecting a game variant
  await page.click('text=STANDARD');

  // then: the selected variant is the correct one
  const selectedVariant = await page.isChecked('text=STANDARD');
  expect(selectedVariant).toBe(true);
});

// user logs in, navigates to lobby, selects a variant, and searches for a game

test('user can log in, navigate to lobby, select a variant, and search for a game', async ({ page }) => {
  // when: navigating to the login page
  await page.goto('http://localhost:8000/login');

  // then: fill in the login form with valid credentials and submit
  await page.fill('input[name="username"]', userCredentials.username);
  await page.fill('input[name="password"]', userCredentials.password);
  await page.click('button[type="submit"]');

  // when: navigating to the lobby page
  await page.click('a[href="/lobby"]');

  // then: the lobby page appears
  const lobbyTitle = page.getByRole('heading', { name: 'Lobby', exact: true });
  await expect(lobbyTitle).toBeVisible();

  // when: selecting a game variant
  await page.click('text=STANDARD');

  // then: the selected variant is the correct one
  const selectedVariant = await page.isChecked('text=STANDARD');
  expect(selectedVariant).toBe(true);

  // when: searching for a game
  const findGameButton = page.getByRole('button', { name: 'Find Game' });
  await findGameButton.click();

  // then: the game search is successful
  await expect(page.getByText('Searching for a opponent...')).toBeVisible();
});