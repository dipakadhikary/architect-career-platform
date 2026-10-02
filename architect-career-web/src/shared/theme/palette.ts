import type { PaletteOptions } from '@mui/material/styles';

/** Forest / brass brand direction — distinct from default purple AI themes. */
export const lightPalette: PaletteOptions = {
  mode: 'light',
  primary: {
    main: '#0B3D2E',
    light: '#1F6B52',
    dark: '#06281E',
    contrastText: '#F4F7F5',
  },
  secondary: {
    main: '#C4A35A',
    light: '#D9C07E',
    dark: '#9A7D3A',
    contrastText: '#1A1A1A',
  },
  background: {
    default: '#F3F6F4',
    paper: '#FFFFFF',
  },
  text: {
    primary: '#14201B',
    secondary: '#4A5C54',
  },
  divider: 'rgba(11, 61, 46, 0.12)',
  error: { main: '#B42318' },
  warning: { main: '#B54708' },
  info: { main: '#175CD3' },
  success: { main: '#067647' },
};

export const darkPalette: PaletteOptions = {
  mode: 'dark',
  primary: {
    main: '#6FCFB0',
    light: '#9BE0C8',
    dark: '#3FA887',
    contrastText: '#06281E',
  },
  secondary: {
    main: '#D4B76A',
    light: '#E4CC8F',
    dark: '#B8963F',
    contrastText: '#1A1A1A',
  },
  background: {
    default: '#0C1411',
    paper: '#15201B',
  },
  text: {
    primary: '#E8F0EC',
    secondary: '#A8B8B0',
  },
  divider: 'rgba(232, 240, 236, 0.12)',
  error: { main: '#F97066' },
  warning: { main: '#FDB022' },
  info: { main: '#84CAFF' },
  success: { main: '#75E0A7' },
};
