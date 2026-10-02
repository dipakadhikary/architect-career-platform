import { test, expect } from '@playwright/test';

test.describe('Authentication & protected routes', () => {
  test('login shell is reachable', async ({ page }) => {
    await page.goto('/login');
    await expect(page.getByRole('heading', { name: 'Sign in' })).toBeVisible();
  });

  test('register shell is reachable', async ({ page }) => {
    await page.goto('/register');
    await expect(page.getByRole('heading', { name: 'Create account' })).toBeVisible();
  });

  test('protected home redirects unauthenticated users to login', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveURL(/\/login/);
  });

  test('protected knowledge redirects to login', async ({ page }) => {
    await page.goto('/knowledge');
    await expect(page).toHaveURL(/\/login/);
  });

  test('protected career redirects to login', async ({ page }) => {
    await page.goto('/career');
    await expect(page).toHaveURL(/\/login/);
  });

  test('protected learning redirects to login', async ({ page }) => {
    await page.goto('/learning');
    await expect(page).toHaveURL(/\/login/);
  });

  test('protected portfolio redirects to login', async ({ page }) => {
    await page.goto('/portfolio');
    await expect(page).toHaveURL(/\/login/);
  });

  test('protected AI redirects to login', async ({ page }) => {
    await page.goto('/ai');
    await expect(page).toHaveURL(/\/login/);
  });
});

test.describe('Public error & offline pages', () => {
  test('offline page renders friendly message', async ({ page }) => {
    await page.goto('/offline');
    await expect(page.getByText(/currently offline/i)).toBeVisible();
  });

  test('unknown routes show not found for guests via login redirect or not found', async ({
    page,
  }) => {
    await page.goto('/this-route-does-not-exist');
    const login = page.getByRole('heading', { name: 'Sign in' });
    const notFound = page.getByText(/not found/i);
    await expect(login.or(notFound)).toBeVisible();
  });
});

test.describe('Responsive layout', () => {
  test('login remains usable on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto('/login');
    await expect(page.getByRole('heading', { name: 'Sign in' })).toBeVisible();
    await expect(page.getByLabel('Email')).toBeVisible();
    await expect(page.getByLabel('Password')).toBeVisible();
  });
});
