import { Alert, AlertTitle, Button, Stack } from '@mui/material';

interface ErrorPanelProps {
  title?: string;
  message: string;
  onRetry?: () => void;
}

export function ErrorPanel({ title = 'Unable to load data', message, onRetry }: ErrorPanelProps) {
  return (
    <Alert
      severity="error"
      action={
        onRetry ? (
          <Button color="inherit" size="small" onClick={onRetry}>
            Retry
          </Button>
        ) : undefined
      }
    >
      <AlertTitle>{title}</AlertTitle>
      <Stack spacing={0.5}>
        <span>{message}</span>
      </Stack>
    </Alert>
  );
}
