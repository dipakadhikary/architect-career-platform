import { Button, Stack } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { PagePlaceholder } from '@/shared/components';
import { appConfig } from '@/app/config/app.config';

export function ForbiddenPage() {
  return (
    <Stack spacing={2}>
      <PagePlaceholder
        title="403 — Forbidden"
        description="You do not have permission to access this resource."
      />
      <Button
        component={RouterLink}
        to={appConfig.routes.home}
        variant="contained"
        sx={{ alignSelf: 'flex-start' }}
      >
        Back to home
      </Button>
    </Stack>
  );
}
