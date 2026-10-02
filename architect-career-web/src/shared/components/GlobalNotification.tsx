import { Alert, Snackbar } from '@mui/material';
import { useNotificationStore } from '@/shared/store/notification.store';

export function GlobalNotification() {
  const open = useNotificationStore((s) => s.open);
  const message = useNotificationStore((s) => s.message);
  const severity = useNotificationStore((s) => s.severity);
  const autoHideDuration = useNotificationStore((s) => s.autoHideDuration);
  const close = useNotificationStore((s) => s.close);

  return (
    <Snackbar
      open={open}
      autoHideDuration={autoHideDuration}
      onClose={(_, reason) => {
        if (reason === 'clickaway') return;
        close();
      }}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
    >
      <Alert onClose={close} severity={severity} variant="filled" sx={{ width: '100%' }}>
        {message}
      </Alert>
    </Snackbar>
  );
}
