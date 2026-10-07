import { ThemeProvider, createTheme } from '@mui/material/styles';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiClientError } from '@/shared/api/types';
import { ForgotPasswordPage } from '@/pages/auth/ForgotPasswordPage';
import { ForgotUserIdPage } from '@/pages/auth/ForgotUserIdPage';
import { LoginPage } from '@/pages/auth/LoginPage';
import { ResetPasswordPage } from '@/pages/auth/ResetPasswordPage';

vi.mock('@/shared/hooks/useAuth', () => ({
  useAuth: () => ({ login: vi.fn() }),
}));

vi.mock('@/shared/hooks/useNotification', () => ({
  useNotification: () => ({ success: vi.fn(), error: vi.fn() }),
}));

vi.mock('@/features/auth/api/auth.api', () => ({
  authApi: {
    forgotUserId: vi.fn(),
    forgotPassword: vi.fn(),
    resetPassword: vi.fn(),
  },
}));

import { authApi } from '@/features/auth/api/auth.api';

const forgotUserId = vi.mocked(authApi.forgotUserId);
const forgotPassword = vi.mocked(authApi.forgotPassword);
const resetPassword = vi.mocked(authApi.resetPassword);

const generic = 'If an account exists for this email address, further instructions have been sent.';

function renderAt(path: string) {
  return render(
    <ThemeProvider theme={createTheme()}>
      <MemoryRouter initialEntries={[path]}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/forgot-user-id" element={<ForgotUserIdPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
        </Routes>
      </MemoryRouter>
    </ThemeProvider>,
  );
}

describe('account recovery pages', () => {
  beforeEach(() => {
    forgotUserId.mockReset();
    forgotPassword.mockReset();
    resetPassword.mockReset();
  });

  it('links login to both recovery pages', async () => {
    const user = userEvent.setup();
    renderAt('/login');

    await user.click(screen.getByRole('link', { name: 'Forgot user ID' }));
    expect(screen.getByRole('heading', { name: 'Forgot user ID' })).toBeInTheDocument();

    renderAt('/login');
    await user.click(screen.getByRole('link', { name: 'Forgot password' }));
    expect(screen.getByRole('heading', { name: 'Forgot password' })).toBeInTheDocument();
  });

  it('validates the forgot user ID email and shows the generic success message', async () => {
    const user = userEvent.setup();
    forgotUserId.mockResolvedValue({ message: generic });
    renderAt('/forgot-user-id');

    await user.click(screen.getByRole('button', { name: 'Send user ID' }));
    expect(await screen.findByText('Enter a valid email address')).toBeInTheDocument();

    await user.type(screen.getByLabelText('Email'), 'ada@acos.local');
    await user.click(screen.getByRole('button', { name: 'Send user ID' }));

    expect(await screen.findByText(generic)).toBeInTheDocument();
    expect(forgotUserId).toHaveBeenCalledWith('ada@acos.local');
    expect(screen.queryByText('ada@acos.local', { selector: '[role="alert"]' })).not.toBeInTheDocument();
  });

  it('returns to login from forgot user ID', async () => {
    const user = userEvent.setup();
    renderAt('/forgot-user-id');
    await user.click(screen.getByRole('link', { name: 'Back to sign in' }));
    expect(screen.getByRole('heading', { name: 'Sign in' })).toBeInTheDocument();
  });

  it('shows a generic forgot-password message and an error when the request fails', async () => {
    const user = userEvent.setup();
    forgotPassword.mockResolvedValueOnce({ message: generic });
    renderAt('/forgot-password');

    await user.type(screen.getByLabelText('Email'), 'ada@acos.local');
    await user.click(screen.getByRole('button', { name: 'Send reset link' }));
    expect(await screen.findByText(generic)).toBeInTheDocument();

    forgotPassword.mockRejectedValueOnce(new Error('Unable to send recovery instructions'));
    await user.click(screen.getByRole('button', { name: 'Send reset link' }));
    expect(await screen.findByText('Unable to send recovery instructions')).toBeInTheDocument();
  });

  it('rejects a reset without a token and a weak password', async () => {
    const user = userEvent.setup();
    renderAt('/reset-password');
    expect(
      screen.getByText('This password reset link is invalid or has expired.'),
    ).toBeInTheDocument();

    renderAt('/reset-password?token=raw-token');
    await user.type(screen.getByLabelText('New password'), 'short');
    await user.type(screen.getByLabelText('Confirm password'), 'short');
    await user.click(screen.getByRole('button', { name: 'Reset password' }));
    expect(await screen.findByText('Password must be at least 12 characters')).toBeInTheDocument();
    expect(resetPassword).not.toHaveBeenCalled();
  });

  it('resets the password and returns to login', async () => {
    const user = userEvent.setup();
    resetPassword.mockResolvedValue(undefined);
    renderAt('/reset-password?token=raw-token');

    await user.type(screen.getByLabelText('New password'), 'Str0ng!Pass12');
    await user.type(screen.getByLabelText('Confirm password'), 'Str0ng!Pass12');
    await user.click(screen.getByRole('button', { name: 'Reset password' }));

    await waitFor(() => {
      expect(resetPassword).toHaveBeenCalledWith({
        token: 'raw-token',
        newPassword: 'Str0ng!Pass12',
        confirmPassword: 'Str0ng!Pass12',
      });
    });
    expect(await screen.findByRole('heading', { name: 'Sign in' })).toBeInTheDocument();
  });

  it('shows an expired token error from the API', async () => {
    const user = userEvent.setup();
    resetPassword.mockRejectedValue(
      new ApiClientError('This password reset link is invalid or has expired.', {
        status: 400,
        code: 'INVALID_TOKEN',
        details: [],
        correlationId: 'corr',
      }),
    );
    renderAt('/reset-password?token=expired');

    await user.type(screen.getByLabelText('New password'), 'Str0ng!Pass12');
    await user.type(screen.getByLabelText('Confirm password'), 'Str0ng!Pass12');
    await user.click(screen.getByRole('button', { name: 'Reset password' }));

    expect(
      await screen.findByText('This password reset link is invalid or has expired.'),
    ).toBeInTheDocument();
  });
});
