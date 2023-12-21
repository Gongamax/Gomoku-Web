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

test('can login', async ({ page }) => {
  // when: navigating to the home page
  await page.goto('http://localhost:8000/');

  // then: the page has a link to the 'me' page
  const playLink = page.getByRole('link', { name: 'Play a Game', exact: true });
  await expect(playLink).toBeVisible();

  // when: navigating to the 'me' page
  await playLink.click();

  // then: the login form appears
  const usernameInput = page.getByLabel('Username');
  const passwordInput = page.getByLabel('Password');
  const loginButton = page.getByRole('button');
  await expect(usernameInput).toBeVisible();
  await expect(passwordInput).toBeVisible();
  await expect(loginButton).toBeVisible();

  // when: providing incorrect credentials
  await usernameInput.fill('alice');
  await passwordInput.fill('Password123');
  await loginButton.click();

  // then: the button get disabled and then enabled again
  await expect(loginButton).toBeDisabled();
  await expect(loginButton).toBeEnabled();

  // and: the error message appears
  await expect(page.getByText('User or password are invalid')).toBeVisible();

  // and: only the username is preserved
  await expect(usernameInput).toHaveValue('alice');
  await expect(passwordInput).toHaveValue('');

  // when: providing correct credentials
  await usernameInput.fill(userCredentials.username);
  await passwordInput.fill(userCredentials.password);
  await loginButton.click();

  // then
  await expect(page.getByText(`Hello ${userCredentials.username}`)).toBeVisible();

  // then: logout
  const logoutButton = page.getByRole('button', { name: 'Logout' });
  await logoutButton.click();
  await expect(page.getByText('Login')).toBeVisible();
});
