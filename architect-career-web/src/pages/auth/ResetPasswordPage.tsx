import { useState } from 'react';
import { Alert, Box, Button, Link, Stack, TextField, Typography } from '@mui/material';
import { Link as RouterLink, useNavigate, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { authApi } from '@/features/auth/api/auth.api';
import {
  resetPasswordSchema,
  type ResetPasswordFormValues,
} from '@/features/auth/schemas/auth.schemas';
import { getErrorMessage } from '@/shared/utils/error';
import { appConfig } from '@/app/config/app.config';

const INVALID_LINK = 'This password reset link is invalid or has expired.';

export function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') ?? '';
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [completed, setCompleted] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: { newPassword: '', confirmPassword: '' },
  });

  const onSubmit = handleSubmit(async (values) => {
    setSubmitting(true);
    setFormError(null);
    try {
      await authApi.resetPassword({
        token,
        newPassword: values.newPassword,
        confirmPassword: values.confirmPassword,
      });
      setCompleted(true);
      navigate(appConfig.routes.login, { replace: true });
    } catch (error) {
      setFormError(getErrorMessage(error, INVALID_LINK));
    } finally {
      setSubmitting(false);
    }
  });

  if (!token) {
    return (
      <Stack spacing={2}>
        <Typography variant="h5" component="h1">
          Reset password
        </Typography>
        <Alert severity="error">{INVALID_LINK}</Alert>
        <Link component={RouterLink} to={appConfig.routes.login} underline="hover">
          Back to sign in
        </Link>
      </Stack>
    );
  }

  return (
    <Box component="form" onSubmit={onSubmit} noValidate>
      <Typography variant="h5" component="h1" gutterBottom>
        Reset password
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Choose a new password with at least 12 characters, including upper and lower case, a digit,
        and a special character.
      </Typography>
      <Stack spacing={2}>
        {completed ? <Alert severity="success">Password reset. Sign in with the new password.</Alert> : null}
        {formError ? <Alert severity="error">{formError}</Alert> : null}
        <TextField
          label="New password"
          type="password"
          autoComplete="new-password"
          fullWidth
          error={Boolean(errors.newPassword)}
          helperText={errors.newPassword?.message}
          {...register('newPassword')}
        />
        <TextField
          label="Confirm password"
          type="password"
          autoComplete="new-password"
          fullWidth
          error={Boolean(errors.confirmPassword)}
          helperText={errors.confirmPassword?.message}
          {...register('confirmPassword')}
        />
        <Button type="submit" variant="contained" size="large" disabled={submitting || completed}>
          {submitting ? 'Resetting…' : 'Reset password'}
        </Button>
        <Link component={RouterLink} to={appConfig.routes.login} underline="hover">
          Back to sign in
        </Link>
      </Stack>
    </Box>
  );
}
