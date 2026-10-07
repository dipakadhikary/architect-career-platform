import { useState } from 'react';
import { Alert, Box, Button, Link, Stack, TextField, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { authApi } from '@/features/auth/api/auth.api';
import {
  recoveryEmailSchema,
  type RecoveryEmailFormValues,
} from '@/features/auth/schemas/auth.schemas';
import { getErrorMessage } from '@/shared/utils/error';
import { appConfig } from '@/app/config/app.config';

const GENERIC_MESSAGE =
  'If an account exists for this email address, further instructions have been sent.';

export function ForgotUserIdPage() {
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [formError, setFormError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RecoveryEmailFormValues>({
    resolver: zodResolver(recoveryEmailSchema),
    defaultValues: { email: '' },
  });

  const onSubmit = handleSubmit(async (values) => {
    setSubmitting(true);
    setFormError(null);
    try {
      const response = await authApi.forgotUserId(values.email);
      setMessage(response.message || GENERIC_MESSAGE);
    } catch (error) {
      setFormError(getErrorMessage(error, 'Unable to send recovery instructions'));
    } finally {
      setSubmitting(false);
    }
  });

  return (
    <Box component="form" onSubmit={onSubmit} noValidate>
      <Typography variant="h5" component="h1" gutterBottom>
        Forgot user ID
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Enter the email address on your account. ACOS signs in with that email address.
      </Typography>
      <Stack spacing={2}>
        {message ? <Alert severity="success">{message}</Alert> : null}
        {formError ? <Alert severity="error">{formError}</Alert> : null}
        <TextField
          label="Email"
          type="email"
          autoComplete="email"
          fullWidth
          error={Boolean(errors.email)}
          helperText={errors.email?.message}
          {...register('email')}
        />
        <Button type="submit" variant="contained" size="large" disabled={submitting}>
          {submitting ? 'Sending…' : 'Send user ID'}
        </Button>
        <Link component={RouterLink} to={appConfig.routes.login} underline="hover">
          Back to sign in
        </Link>
      </Stack>
    </Box>
  );
}
