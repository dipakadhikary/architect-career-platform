import { Box, Tab, Tabs } from '@mui/material';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';

const NAV_ITEMS = [
  { label: 'Dashboard', path: '/career' },
  { label: 'Applications', path: '/career/applications' },
  { label: 'Companies', path: '/career/companies' },
  { label: 'Recruiters', path: '/career/recruiters' },
] as const;

function resolveActiveTab(pathname: string): string {
  if (pathname.startsWith('/career/applications')) {
    return '/career/applications';
  }
  const match = NAV_ITEMS.find(
    (item) => item.path === pathname || (item.path !== '/career' && pathname.startsWith(item.path)),
  );
  return match?.path ?? '/career';
}

export function CareerShell() {
  const location = useLocation();
  const navigate = useNavigate();
  const activeTab = resolveActiveTab(location.pathname);

  return (
    <Box>
      <Tabs
        value={activeTab}
        onChange={(_, value: string) => navigate(value)}
        sx={{ mb: 3, borderBottom: 1, borderColor: 'divider' }}
        variant="scrollable"
        scrollButtons="auto"
      >
        {NAV_ITEMS.map((item) => (
          <Tab key={item.path} label={item.label} value={item.path} />
        ))}
      </Tabs>
      <Outlet />
    </Box>
  );
}
