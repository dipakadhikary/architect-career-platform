import { Box, Typography } from '@mui/material';
import { appConfig } from '@/app/config/app.config';

export function Footer() {
  const year = new Date().getFullYear();

  return (
    <Box
      component="footer"
      sx={{
        mt: 'auto',
        px: 3,
        py: 2,
        borderTop: 1,
        borderColor: 'divider',
        display: 'flex',
        flexWrap: 'wrap',
        gap: 1,
        justifyContent: 'space-between',
        alignItems: 'center',
      }}
    >
      <Typography variant="caption" color="text.secondary">
        © {year} {appConfig.name}. Architect Career Operating System.
      </Typography>
      <Typography
        variant="caption"
        color="text.secondary"
        sx={{ fontFamily: '"IBM Plex Mono", monospace' }}
      >
        v{appConfig.version}
      </Typography>
    </Box>
  );
}
