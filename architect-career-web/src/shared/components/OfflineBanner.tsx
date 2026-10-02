import { Alert, Button, Slide } from '@mui/material';
import { useEffect, useRef } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { useNetworkStatus } from '@/shared/hooks/useNetworkStatus';
import { useNotification } from '@/shared/hooks/useNotification';

export function OfflineBanner() {
  const { offline, online } = useNetworkStatus();
  const queryClient = useQueryClient();
  const { success } = useNotification();
  const wasOffline = useRef(false);

  useEffect(() => {
    if (offline) {
      wasOffline.current = true;
      return;
    }
    if (wasOffline.current && online) {
      wasOffline.current = false;
      success('Back online. Refreshing data…');
      void queryClient.invalidateQueries();
    }
  }, [offline, online, queryClient, success]);

  return (
    <Slide direction="down" in={offline} mountOnEnter unmountOnExit>
      <Alert
        severity="warning"
        sx={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          zIndex: (theme) => theme.zIndex.snackbar + 1,
          borderRadius: 0,
        }}
        action={
          <Button color="inherit" size="small" onClick={() => window.location.reload()}>
            Retry
          </Button>
        }
      >
        You are currently offline. Some actions may be unavailable until your connection returns.
      </Alert>
    </Slide>
  );
}
