import { Suspense, type ComponentType, type ReactNode, lazy } from 'react';
import { LoadingSpinner } from '@/shared/components';

export function RouteFallback() {
  return <LoadingSpinner fullScreen label="Loading page…" />;
}

export function withSuspense(element: ReactNode) {
  return <Suspense fallback={<RouteFallback />}>{element}</Suspense>;
}

/**
 * Lazy-load a named export from a module for route-based code splitting.
 */
export function lazyNamed(factory: () => Promise<Record<string, unknown>>, exportName: string) {
  return lazy(async () => {
    const module = await factory();
    const Component = module[exportName];
    if (typeof Component !== 'function' && typeof Component !== 'object') {
      throw new Error(`Route export "${exportName}" is not a React component`);
    }
    return { default: Component as ComponentType };
  });
}
