import { useState } from 'react';
import { Alert, Box, Button, Link, Stack, TextField, Typography } from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { registerSchema, type RegisterFormValues } from '@/features/auth/schemas/auth.schemas';
import { authApi } from '@/features/auth/api/auth.api';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { appConfig } from '@/app/config/app.config';

export function RegisterPage() {
  const navigate = useNavigate();
  const { success, error: notifyError } = useNotification();
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      confirmPassword: '',
    },
  });

  const onSubmit = handleSubmit(async (values) => {
    setSubmitting(true);
    try {
      await authApi.register({
        email: values.email,
        firstName: values.firstName,
        lastName: values.lastName,
        password: values.password,
      });
      success('Account created. Please sign in.');
      navigate(appConfig.routes.login, { replace: true });
    } catch (error) {
      notifyError(getErrorMessage(error, 'Unable to create account'));
    } finally {
      setSubmitting(false);
    }
  });

  return (
    <Box component="form" onSubmit={onSubmit} noValidate>
      <Typography variant="h5" component="h1" gutterBottom>
        Create account
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Register to start tracking learning, career, and portfolio progress.
      </Typography>

      <Stack spacing={2}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="First name"
            fullWidth
            error={Boolean(errors.firstName)}
            helperText={errors.firstName?.message}
            {...register('firstName')}
          />
          <TextField
            label="Last name"
            fullWidth
            error={Boolean(errors.lastName)}
            helperText={errors.lastName?.message}
            {...register('lastName')}
          />
        </Stack>
        <TextField
          label="Email"
          type="email"
          autoComplete="email"
          fullWidth
          error={Boolean(errors.email)}
          helperText={errors.email?.message}
          {...register('email')}
        />
        <TextField
          label="Password"
          type="password"
          autoComplete="new-password"
          fullWidth
          error={Boolean(errors.password)}
          helperText={errors.password?.message}
          {...register('password')}
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
        <Button type="submit" variant="contained" size="large" disabled={submitting}>
          {submitting ? 'Creating…' : 'Create account'}
        </Button>
        <Alert severity="info" variant="outlined">
          Already registered?{' '}
          <Link component={RouterLink} to={appConfig.routes.login} underline="hover">
            Sign in
          </Link>
        </Alert>
      </Stack>
    </Box>
  );
}
