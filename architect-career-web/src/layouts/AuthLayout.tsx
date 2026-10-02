import { Box, Paper, Typography } from '@mui/material';
import { Outlet } from 'react-router-dom';
import { appConfig } from '@/app/config/app.config';
import { Footer } from './components/Footer';

/**
 * Unauthenticated shell — brand + content outlet for login/register routes.
 */
export function AuthLayout() {
  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <Box
        sx={{
          flex: 1,
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', md: '1.1fr 0.9fr' },
          minHeight: 0,
        }}
      >
        <Box
          sx={{
            display: { xs: 'none', md: 'flex' },
            flexDirection: 'column',
            justifyContent: 'flex-end',
            p: 6,
            background: 'linear-gradient(160deg, #06281E 0%, #0B3D2E 45%, #1F6B52 100%)',
            color: '#E8F0EC',
            position: 'relative',
            overflow: 'hidden',
            '&::after': {
              content: '""',
              position: 'absolute',
              inset: 0,
              background:
                'radial-gradient(circle at 80% 20%, rgba(196, 163, 90, 0.28), transparent 40%)',
              pointerEvents: 'none',
            },
          }}
        >
          <Box sx={{ position: 'relative', zIndex: 1, maxWidth: 420 }}>
            <Typography
              variant="overline"
              sx={{ color: 'secondary.light', letterSpacing: '0.14em' }}
            >
              Architect Career OS
            </Typography>
            <Typography variant="h2" sx={{ mt: 1, mb: 2, color: 'inherit' }}>
              {appConfig.name}
            </Typography>
            <Typography variant="body1" sx={{ opacity: 0.88, maxWidth: 360 }}>
              Your operating system for learning, career progression, and professional portfolio —
              built for architects.
            </Typography>
          </Box>
        </Box>

        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            p: { xs: 3, sm: 4 },
          }}
        >
          <Paper
            elevation={0}
            sx={{
              width: '100%',
              maxWidth: 440,
              p: { xs: 3, sm: 4 },
              border: 1,
              borderColor: 'divider',
              bgcolor: 'background.paper',
            }}
          >
            <Typography
              variant="h5"
              sx={{ display: { xs: 'block', md: 'none' }, mb: 3, fontWeight: 700 }}
            >
              {appConfig.name}
            </Typography>
            <Outlet />
          </Paper>
        </Box>
      </Box>
      <Footer />
    </Box>
  );
}
