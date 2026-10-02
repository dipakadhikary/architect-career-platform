import { Box, Tab, Tabs } from '@mui/material';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAiAvailability } from '../hooks/useAiHealth';
import { AiUnavailableBanner } from '../components/AiUnavailableBanner';

const NAV_ITEMS = [
  { label: 'Dashboard', path: '/ai' },
  { label: 'Chat', path: '/ai/chat' },
  { label: 'Knowledge', path: '/ai/knowledge' },
  { label: 'Learning', path: '/ai/learning' },
  { label: 'Career', path: '/ai/career' },
  { label: 'Portfolio', path: '/ai/portfolio' },
] as const;

function resolveActiveTab(pathname: string): string {
  const match = NAV_ITEMS.find(
    (item) => item.path === pathname || (item.path !== '/ai' && pathname.startsWith(item.path)),
  );
  return match?.path ?? '/ai';
}

export function AiShell() {
  const location = useLocation();
  const navigate = useNavigate();
  const { toggleEnabled, canInvoke, unavailableReason, refetch, isError } = useAiAvailability();
  const activeTab = resolveActiveTab(location.pathname);

  return (
    <Box>
      <Tabs
        value={activeTab}
        onChange={(_, value: string) => navigate(value)}
        sx={{ mb: 2, borderBottom: 1, borderColor: 'divider' }}
        variant="scrollable"
        scrollButtons="auto"
      >
        {NAV_ITEMS.map((item) => (
          <Tab key={item.path} label={item.label} value={item.path} />
        ))}
      </Tabs>

      {!toggleEnabled || !canInvoke || isError ? (
        <AiUnavailableBanner
          message={unavailableReason ?? 'AI Platform is currently unavailable.'}
          onRetry={() => void refetch()}
        />
      ) : null}

      <Outlet />
    </Box>
  );
}
