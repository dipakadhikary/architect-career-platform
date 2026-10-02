import { Button, Stack, Typography } from '@mui/material';
import WifiOffOutlinedIcon from '@mui/icons-material/WifiOffOutlined';
import { PagePlaceholder } from '@/shared/components';

export function OfflinePage() {
  return (
    <Stack
      spacing={2}
      sx={{
        minHeight: '100vh',
        alignItems: 'center',
        justifyContent: 'center',
        px: 3,
        textAlign: 'center',
      }}
    >
      <WifiOffOutlinedIcon sx={{ fontSize: 56, color: 'text.secondary' }} />
      <PagePlaceholder
        title="You are currently offline."
        description="Cached pages may still be available. We will reconnect automatically when your network returns."
      />
      <Typography variant="body2" color="text.secondary">
        Your work in ACOS will sync once connectivity is restored.
      </Typography>
      <Button variant="contained" onClick={() => window.location.reload()}>
        Try again
      </Button>
    </Stack>
  );
}
