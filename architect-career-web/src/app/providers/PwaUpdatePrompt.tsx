import { useEffect } from 'react';
import { useRegisterSW } from 'virtual:pwa-register/react';
import { useNotification } from '@/shared/hooks/useNotification';

/**
 * Registers the service worker and prompts when a new app version is ready.
 */
export function PwaUpdatePrompt() {
  const { info } = useNotification();
  const {
    needRefresh: [needRefresh, setNeedRefresh],
    updateServiceWorker,
  } = useRegisterSW({
    onRegisteredSW() {
      // Registered successfully — no console logging of sensitive data.
    },
    onRegisterError() {
      // Fail silently in production UX; offline cache simply unavailable.
    },
  });

  useEffect(() => {
    if (!needRefresh) return;
    info('A new version of ACOS is available. Reload to update.');
    const timer = window.setTimeout(() => {
      void updateServiceWorker(true);
      setNeedRefresh(false);
    }, 8_000);
    return () => window.clearTimeout(timer);
  }, [needRefresh, info, setNeedRefresh, updateServiceWorker]);

  return null;
}
