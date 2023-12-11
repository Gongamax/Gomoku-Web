import { test, expect } from '@playwright/test';

test('can navigate to about page', async ({ page }) => {
  // when: navigating to the home page
  await page.goto('http://localhost:8000/');

  // then: the page has a link to the 'About' page
  const aboutLink = page.getByRole('link', { name: 'About', exact: true });
  await expect(aboutLink).toBeVisible();

  // when: navigating to the 'About' page
  await aboutLink.click();

  // then: the about page appears
  const aboutTitle = page.getByRole('heading', { name: 'About', exact: true });
  await expect(aboutTitle).toBeVisible();
});