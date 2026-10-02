import { Box, CircularProgress, Typography } from '@mui/material';

interface LoadingSpinnerProps {
  label?: string;
  fullScreen?: boolean;
  size?: number;
}

export function LoadingSpinner({
  label = 'Loading…',
  fullScreen = false,
  size = 40,
}: LoadingSpinnerProps) {
  return (
    <Box
      role="status"
      aria-live="polite"
      aria-busy="true"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 2,
        minHeight: fullScreen ? '100vh' : 240,
        width: '100%',
      }}
    >
      <CircularProgress size={size} thickness={4} />
      {label ? (
        <Typography variant="body2" color="text.secondary">
          {label}
        </Typography>
      ) : null}
    </Box>
  );
}
