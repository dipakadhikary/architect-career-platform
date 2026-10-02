import type { ReactNode } from 'react';
import { ErrorBoundary, GlobalNotification, OfflineBanner } from '@/shared/components';
import { ThemeProvider } from './ThemeProvider';
import { QueryProvider } from './QueryProvider';
import { PwaUpdatePrompt } from './PwaUpdatePrompt';

interface AppProvidersProps {
  children: ReactNode;
}

/** Composition root for theme, data fetching, errors, offline, and notifications. */
export function AppProviders({ children }: AppProvidersProps) {
  return (
    <ErrorBoundary fallbackTitle="Application error">
      <ThemeProvider>
        <QueryProvider>
          <OfflineBanner />
          {children}
          <GlobalNotification />
          <PwaUpdatePrompt />
        </QueryProvider>
      </ThemeProvider>
    </ErrorBoundary>
  );
}
