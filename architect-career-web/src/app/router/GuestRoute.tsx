import { Navigate, Outlet } from 'react-router-dom';
import { appConfig } from '@/app/config/app.config';
import { useAuth } from '@/shared/hooks/useAuth';
import { LoadingSpinner } from '@/shared/components';

/**
 * Redirects authenticated users away from guest-only screens (login/register).
 */
export function GuestRoute() {
  const { isAuthenticated, isHydrated } = useAuth();

  if (!isHydrated) {
    return <LoadingSpinner fullScreen label="Loading…" />;
  }

  if (isAuthenticated) {
    return <Navigate to={appConfig.routes.home} replace />;
  }

  return <Outlet />;
}
