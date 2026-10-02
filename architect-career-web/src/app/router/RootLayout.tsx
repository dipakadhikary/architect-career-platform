import { useEffect } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { appConfig } from '@/app/config/app.config';
import { useAuthStore } from '@/features/auth/store/auth.store';
import { useNotification } from '@/shared/hooks/useNotification';

/**
 * Top-level route layout: hydrates auth from storage and handles session expiry.
 */
export function RootLayout() {
  const hydrate = useAuthStore((s) => s.hydrate);
  const clearSession = useAuthStore((s) => s.clearSession);
  const navigate = useNavigate();
  const { warning } = useNotification();

  useEffect(() => {
    hydrate();
  }, [hydrate]);

  useEffect(() => {
    const onExpired = () => {
      clearSession();
      warning('Your session expired. Please sign in again.');
      navigate(appConfig.routes.login, { replace: true });
    };

    window.addEventListener('acos:session-expired', onExpired);
    return () => window.removeEventListener('acos:session-expired', onExpired);
  }, [clearSession, navigate, warning]);

  return <Outlet />;
}
