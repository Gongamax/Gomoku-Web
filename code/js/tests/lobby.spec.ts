import { test, expect } from '@playwright/test';

test('user can log in, navigate to lobby, and select a variant', async ({ page }) => {
  // when: navigating to the login page
  await page.goto('http://localhost:8000/login');

  // then: fill in the login form with valid credentials and submit
  await page.fill('input[name="username"]', 'backfire');
  await page.fill('input[name="password"]', 'Abacate345');
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