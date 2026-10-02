import { create } from 'zustand';
import { storage } from '@/shared/utils/storage';

export type ThemeMode = 'light' | 'dark';

const THEME_KEY = 'acos.themeMode';

function resolveInitialMode(): ThemeMode {
  const stored = storage.getString(THEME_KEY);
  if (stored === 'light' || stored === 'dark') return stored;

  if (typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark';
  }
  return 'light';
}

interface ThemeState {
  mode: ThemeMode;
  setMode: (mode: ThemeMode) => void;
  toggleMode: () => void;
}

export const useThemeStore = create<ThemeState>((set, get) => ({
  mode: resolveInitialMode(),
  setMode: (mode) => {
    storage.setString(THEME_KEY, mode);
    set({ mode });
  },
  toggleMode: () => {
    const next: ThemeMode = get().mode === 'light' ? 'dark' : 'light';
    storage.setString(THEME_KEY, next);
    set({ mode: next });
  },
}));
