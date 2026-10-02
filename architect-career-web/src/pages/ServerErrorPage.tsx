import { Button, Stack } from '@mui/material';
import { isRouteErrorResponse, useRouteError } from 'react-router-dom';
import { PagePlaceholder } from '@/shared/components';
import { appConfig } from '@/app/config/app.config';

export function ServerErrorPage() {
  const error = useRouteError();
  let message = 'An unexpected error occurred. Please try again.';

  if (isRouteErrorResponse(error)) {
    message = error.statusText || message;
  } else if (error instanceof Error && error.message) {
    message = error.message;
  }

  return (
    <Stack spacing={2} sx={{ p: 3 }}>
      <PagePlaceholder title="Something went wrong" description={message} />
      <Stack direction="row" spacing={1}>
        <Button variant="contained" onClick={() => window.location.assign(appConfig.routes.home)}>
          Back to dashboard
        </Button>
        <Button variant="outlined" onClick={() => window.location.reload()}>
          Retry
        </Button>
      </Stack>
    </Stack>
  );
}
