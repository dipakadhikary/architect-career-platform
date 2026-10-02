import { Alert, AlertTitle, Button } from '@mui/material';

interface AiUnavailableBannerProps {
  message?: string;
  onRetry?: () => void;
}

export function AiUnavailableBanner({
  message = 'AI Platform is currently unavailable.',
  onRetry,
}: AiUnavailableBannerProps) {
  return (
    <Alert
      severity="info"
      sx={{ mb: 2 }}
      action={
        onRetry ? (
          <Button color="inherit" size="small" onClick={onRetry}>
            Retry
          </Button>
        ) : undefined
      }
    >
      <AlertTitle>AI unavailable</AlertTitle>
      {message}
    </Alert>
  );
}
