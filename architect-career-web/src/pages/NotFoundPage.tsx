import { Button, Stack } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { PagePlaceholder } from '@/shared/components';
import { appConfig } from '@/app/config/app.config';

export function NotFoundPage() {
  return (
    <Stack spacing={2}>
      <PagePlaceholder
        title="Page not found"
        description="The route you requested does not exist."
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
