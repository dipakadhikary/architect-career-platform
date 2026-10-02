import { Button, Stack } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { PagePlaceholder } from '@/shared/components';
import { appConfig } from '@/app/config/app.config';

export function UnauthorizedPage() {
  return (
    <Stack spacing={2}>
      <PagePlaceholder
        title="Access denied"
        description="You do not have permission to view this area."
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
