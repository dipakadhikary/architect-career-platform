import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { appConfig } from '@/app/config/app.config';
import { useAuth } from '@/shared/hooks/useAuth';
import { LoadingSpinner } from '@/shared/components';

interface ProtectedRouteProps {
  /** When set, user must have at least one of these roles. */
  roles?: Array<'USER' | 'ADMIN'>;
}

/**
 * Route guard that requires an authenticated session (and optional roles).
 */
export function ProtectedRoute({ roles }: ProtectedRouteProps) {
  const location = useLocation();
  const { isAuthenticated, isHydrated, user } = useAuth();

  if (!isHydrated) {
    return <LoadingSpinner fullScreen label="Restoring session…" />;
  }

  if (!isAuthenticated) {
    return <Navigate to={appConfig.routes.login} replace state={{ from: location }} />;
  }

  if (roles?.length && user) {
    const allowed = roles.some((role) => user.roles.includes(role));
    if (!allowed) {
      return <Navigate to={appConfig.routes.unauthorized} replace />;
    }
  }

  return <Outlet />;
}
