import { AppBar, Avatar, Box, IconButton, Toolbar, Tooltip, Typography } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import DarkModeOutlinedIcon from '@mui/icons-material/DarkModeOutlined';
import LightModeOutlinedIcon from '@mui/icons-material/LightModeOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import { appConfig } from '@/app/config/app.config';
import { useAuth } from '@/shared/hooks/useAuth';
import { useThemeMode } from '@/shared/hooks/useThemeMode';
import { useNotification } from '@/shared/hooks/useNotification';

interface TopNavProps {
  onMenuClick: () => void;
  title?: string;
}

export function TopNav({ onMenuClick, title = 'Workspace' }: TopNavProps) {
  const { user, logout } = useAuth();
  const { isDark, toggleMode } = useThemeMode();
  const { success, error } = useNotification();

  const initials = user
    ? `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase()
    : 'AC';

  const handleLogout = async () => {
    try {
      await logout();
      success('Signed out');
    } catch {
      error('Unable to sign out cleanly');
    }
  };

  return (
    <AppBar
      position="sticky"
      sx={{
        borderBottom: 1,
        borderColor: 'divider',
        bgcolor: 'background.paper',
        color: 'text.primary',
      }}
    >
      <Toolbar sx={{ gap: 1 }}>
        <IconButton
          edge="start"
          color="inherit"
          aria-label="Open navigation"
          onClick={onMenuClick}
          sx={{ display: { md: 'none' } }}
        >
          <MenuIcon />
        </IconButton>

        <Box sx={{ flex: 1, minWidth: 0 }}>
          <Typography variant="h6" noWrap>
            {title}
          </Typography>
          <Typography
            variant="caption"
            color="text.secondary"
            sx={{ display: { xs: 'none', sm: 'block' } }}
          >
            {appConfig.name} v{appConfig.version}
          </Typography>
        </Box>

        <Tooltip title={isDark ? 'Switch to light mode' : 'Switch to dark mode'}>
          <IconButton color="inherit" onClick={toggleMode} aria-label="Toggle color mode">
            {isDark ? <LightModeOutlinedIcon /> : <DarkModeOutlinedIcon />}
          </IconButton>
        </Tooltip>

        <Tooltip title={user ? `${user.firstName} ${user.lastName}` : 'Account'}>
          <Avatar
            sx={{
              width: 36,
              height: 36,
              bgcolor: 'primary.main',
              color: 'primary.contrastText',
              fontSize: 14,
              fontWeight: 600,
            }}
          >
            {initials}
          </Avatar>
        </Tooltip>

        <Tooltip title="Sign out">
          <IconButton color="inherit" onClick={handleLogout} aria-label="Sign out">
            <LogoutOutlinedIcon />
          </IconButton>
        </Tooltip>
      </Toolbar>
    </AppBar>
  );
}
