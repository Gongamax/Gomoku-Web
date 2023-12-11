import { test, expect } from '@playwright/test';

test('can navigate to ranking page', async ({ page }) => {
  // when: navigating to the home page
  await page.goto('http://localhost:8000/');

  // then: the page has a link to the 'Ranking' page
  const rankingLink = page.getByRole('link', { name: 'Ranking', exact: true });
  await expect(rankingLink).toBeVisible();

  // when: navigating to the 'Ranking' page
  await rankingLink.click();

  // then: the ranking page appears
  const rankingTitle = page.getByRole('heading', { name: 'Player Rankings', exact: true });
  await expect(rankingTitle).toBeVisible();

  // and: the ranking table appears
  const rankingTable = page.getByRole('table');
  await expect(rankingTable).toBeVisible();
});

test('ranking page has at least one row of data', async ({ page }) => {
  // when: navigating to the home page
  await page.goto('http://localhost:8000/');

  // then: the page has a link to the 'Ranking' page
  const rankingLink = page.getByRole('link', { name: 'Ranking', exact: true });
  await expect(rankingLink).toBeVisible();

  // when: navigating to the 'Ranking' page
  await rankingLink.click();

  // then: the ranking page appears
  const rankingTitle = page.getByRole('heading', { name: 'Player Rankings', exact: true });
  await expect(rankingTitle).toBeVisible();

  // and: the ranking table appears
  const rankingTable = page.getByRole('table');
  await expect(rankingTable).toBeVisible();

  // and: the ranking table has at least one row of data
  const rows = await page.$$(`table > tbody > tr`);
  await expect(rows.length).toBeGreaterThan(0);
});