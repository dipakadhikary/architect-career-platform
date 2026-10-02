import { createTheme, type ThemeOptions } from '@mui/material/styles';
import type { ThemeMode } from '@/shared/store/theme.store';
import { darkPalette, lightPalette } from './palette';

const sharedTypography: ThemeOptions['typography'] = {
  fontFamily: '"DM Sans", "Segoe UI", sans-serif',
  h1: { fontWeight: 700, letterSpacing: '-0.02em' },
  h2: { fontWeight: 700, letterSpacing: '-0.02em' },
  h3: { fontWeight: 600, letterSpacing: '-0.01em' },
  h4: { fontWeight: 600 },
  h5: { fontWeight: 600 },
  h6: { fontWeight: 600 },
  button: { textTransform: 'none', fontWeight: 600 },
  overline: {
    fontFamily: '"IBM Plex Mono", monospace',
    letterSpacing: '0.08em',
  },
};

const sharedShape: ThemeOptions['shape'] = {
  borderRadius: 10,
};

export function createAppTheme(mode: ThemeMode) {
  return createTheme({
    palette: mode === 'light' ? lightPalette : darkPalette,
    typography: sharedTypography,
    shape: sharedShape,
    components: {
      MuiCssBaseline: {
        styleOverrides: {
          body: {
            minHeight: '100vh',
            backgroundImage:
              mode === 'light'
                ? 'radial-gradient(ellipse at top left, rgba(11, 61, 46, 0.06), transparent 50%), radial-gradient(ellipse at bottom right, rgba(196, 163, 90, 0.08), transparent 45%)'
                : 'radial-gradient(ellipse at top left, rgba(111, 207, 176, 0.08), transparent 50%), radial-gradient(ellipse at bottom right, rgba(212, 183, 106, 0.06), transparent 45%)',
            backgroundAttachment: 'fixed',
          },
          code: {
            fontFamily: '"IBM Plex Mono", monospace',
          },
        },
      },
      MuiButton: {
        defaultProps: {
          disableElevation: true,
        },
        styleOverrides: {
          root: {
            borderRadius: 8,
          },
        },
      },
      MuiAppBar: {
        defaultProps: {
          elevation: 0,
          color: 'transparent',
        },
      },
      MuiDrawer: {
        styleOverrides: {
          paper: {
            borderRight: '1px solid',
            borderColor: 'divider',
          },
        },
      },
    },
  });
}
