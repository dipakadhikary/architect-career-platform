import { Backdrop, CircularProgress, Stack, Typography } from '@mui/material';

interface LoadingOverlayProps {
  open: boolean;
  label?: string;
}

export function LoadingOverlay({ open, label = 'Loading…' }: LoadingOverlayProps) {
  return (
    <Backdrop open={open} sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.modal + 1 }}>
      <Stack spacing={2} alignItems="center">
        <CircularProgress color="inherit" />
        <Typography variant="body2">{label}</Typography>
      </Stack>
    </Backdrop>
  );
}
