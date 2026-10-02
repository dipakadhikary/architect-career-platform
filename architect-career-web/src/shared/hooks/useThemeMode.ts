import { useThemeStore } from '@/shared/store/theme.store';

export function useThemeMode() {
  const mode = useThemeStore((s) => s.mode);
  const setMode = useThemeStore((s) => s.setMode);
  const toggleMode = useThemeStore((s) => s.toggleMode);

  return { mode, setMode, toggleMode, isDark: mode === 'dark' };
}
