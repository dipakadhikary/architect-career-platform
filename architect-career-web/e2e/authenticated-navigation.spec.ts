import { test, expect } from '@playwright/test';

/**
 * Authenticated module smoke coverage.
 * When backend credentials are unavailable, tests seed a synthetic session in localStorage
 * to exercise protected chrome and navigation. API-backed CRUD still requires a live API.
 */
async function seedAuthenticatedSession(page: import('@playwright/test').Page) {
  await page.addInitScript(() => {
    const user = {
      id: '00000000-0000-4000-8000-000000000001',
      email: 'e2e@acos.local',
      firstName: 'E2E',
      lastName: 'User',
      enabled: true,
      roles: ['USER'],
    };
    localStorage.setItem('acos.accessToken', 'e2e-access-token');
    localStorage.setItem('acos.refreshToken', 'e2e-refresh-token');
    localStorage.setItem('acos.tokenType', 'Bearer');
    localStorage.setItem('acos.expiresAt', String(Date.now() + 60 * 60 * 1000));
    localStorage.setItem('acos.user', JSON.stringify(user));
  });
}

test.describe('Authenticated navigation chrome', () => {
  test.beforeEach(async ({ page }) => {
    await seedAuthenticatedSession(page);
  });

  test('dashboard chrome loads with primary navigation', async ({ page }) => {
    await page.goto('/');
    const nav = page.getByRole('navigation', { name: 'Primary' });
    await expect(nav).toBeVisible();
    await expect(nav.getByRole('link', { name: 'Dashboard' })).toBeVisible();
    await expect(nav.getByRole('link', { name: 'Knowledge' })).toBeVisible();
    await expect(nav.getByRole('link', { name: 'Tutorials' })).toBeVisible();
    await expect(nav.getByRole('link', { name: 'Learning' })).toBeVisible();
    await expect(nav.getByRole('link', { name: 'Portfolio' })).toBeVisible();
    await expect(nav.getByRole('link', { name: 'Career' })).toBeVisible();
    await expect(nav.getByRole('link', { name: 'AI' })).toBeVisible();
  });

  test('can navigate between major modules', async ({ page }) => {
    await page.goto('/');
    const nav = page.getByRole('navigation', { name: 'Primary' });
    await nav.getByRole('link', { name: 'Knowledge' }).click();
    await expect(page).toHaveURL(/\/knowledge/);
    await nav.getByRole('link', { name: 'Tutorials' }).click();
    await expect(page).toHaveURL(/\/tutorials/);
    await nav.getByRole('link', { name: 'Learning' }).click();
    await expect(page).toHaveURL(/\/learning/);
    await nav.getByRole('link', { name: 'Portfolio' }).click();
    await expect(page).toHaveURL(/\/portfolio/);
    await nav.getByRole('link', { name: 'Career' }).click();
    await expect(page).toHaveURL(/\/career/);
    await nav.getByRole('link', { name: 'AI' }).click();
    await expect(page).toHaveURL(/\/ai/);
  });

  test('logout control is available in top navigation', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('button', { name: 'Sign out' })).toBeVisible();
  });

  test('logout clears session and returns to login', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'Sign out' }).click();
    await expect(page).toHaveURL(/\/login/);
  });
});
