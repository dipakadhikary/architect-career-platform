import { useState } from 'react';
import { Box, Link } from '@mui/material';
import { Outlet } from 'react-router-dom';
import { appConfig } from '@/app/config/app.config';
import { Sidebar } from './components/Sidebar';
import { TopNav } from './components/TopNav';
import { Footer } from './components/Footer';

/**
 * Authenticated application chrome: sidebar, top nav, content, footer.
 */
export function AppLayout() {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <Link
        href="#main-content"
        sx={{
          position: 'absolute',
          left: -9999,
          zIndex: (theme) => theme.zIndex.tooltip + 1,
          bgcolor: 'background.paper',
          color: 'text.primary',
          px: 2,
          py: 1,
          '&:focus': { left: 16, top: 16 },
        }}
      >
        Skip to main content
      </Link>

      <Sidebar mobileOpen={mobileOpen} onMobileClose={() => setMobileOpen(false)} />

      <Box
        component="main"
        id="main-content"
        tabIndex={-1}
        sx={{
          flexGrow: 1,
          width: { md: `calc(100% - ${appConfig.layout.drawerWidth}px)` },
          display: 'flex',
          flexDirection: 'column',
          minWidth: 0,
          outline: 'none',
        }}
      >
        <TopNav onMenuClick={() => setMobileOpen((open) => !open)} />
        <Box sx={{ flex: 1, px: { xs: 2, md: 3 }, py: 1 }}>
          <Outlet />
        </Box>
        <Footer />
      </Box>
    </Box>
  );
}
