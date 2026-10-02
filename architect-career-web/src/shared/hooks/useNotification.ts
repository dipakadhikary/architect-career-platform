import { useCallback } from 'react';
import { useNotificationStore, type NotificationSeverity } from '@/shared/store/notification.store';

export function useNotification() {
  const notify = useNotificationStore((s) => s.notify);
  const close = useNotificationStore((s) => s.close);

  const show = useCallback(
    (message: string, severity: NotificationSeverity = 'info') => {
      notify({ message, severity });
    },
    [notify],
  );

  return {
    notify,
    close,
    success: (message: string) => show(message, 'success'),
    info: (message: string) => show(message, 'info'),
    warning: (message: string) => show(message, 'warning'),
    error: (message: string) => show(message, 'error'),
  };
}
