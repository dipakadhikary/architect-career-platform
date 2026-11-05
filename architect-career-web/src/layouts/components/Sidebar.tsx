import type { ReactNode } from 'react';
import { useEffect, useState } from 'react';
import {
  Box,
  Collapse,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  useMediaQuery,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import DashboardOutlinedIcon from '@mui/icons-material/DashboardOutlined';
import SchoolOutlinedIcon from '@mui/icons-material/SchoolOutlined';
import WorkOutlineIcon from '@mui/icons-material/WorkOutlineOutlined';
import FolderOpenOutlinedIcon from '@mui/icons-material/FolderOpenOutlined';
import MenuBookOutlinedIcon from '@mui/icons-material/MenuBookOutlined';
import AutoAwesomeOutlinedIcon from '@mui/icons-material/AutoAwesomeOutlined';
import MailOutlineIcon from '@mui/icons-material/MailOutline';
import MenuBookIcon from '@mui/icons-material/AutoStoriesOutlined';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { NavLink, useLocation } from 'react-router-dom';
import { appConfig } from '@/app/config/app.config';
import { routePrefetchers } from '@/app/router/prefetch';
import { TutorialSidebarTree } from '@/features/tutorials/components/TutorialSidebarTree';

export interface NavItem {
  label: string;
  path: string;
  icon: ReactNode;
}

const defaultNavItems: NavItem[] = [
  { label: 'Dashboard', path: '/', icon: <DashboardOutlinedIcon /> },
  { label: 'Learning', path: '/learning', icon: <SchoolOutlinedIcon /> },
  { label: 'Career', path: '/career', icon: <WorkOutlineIcon /> },
  { label: 'Portfolio', path: '/portfolio', icon: <FolderOpenOutlinedIcon /> },
  { label: 'Knowledge', path: '/knowledge', icon: <MenuBookOutlinedIcon /> },
  { label: 'Tutorials', path: '/tutorials', icon: <MenuBookIcon /> },
  { label: 'AI', path: '/ai', icon: <AutoAwesomeOutlinedIcon /> },
  { label: 'Messages', path: '/notifications/templates', icon: <MailOutlineIcon /> },
];

interface SidebarProps {
  mobileOpen: boolean;
  onMobileClose: () => void;
  navItems?: NavItem[];
}

function prefetchRoute(path: string) {
  const prefetcher = routePrefetchers[path];
  if (prefetcher) {
    void prefetcher();
  }
}

function isTutorialsPath(pathname: string): boolean {
  return pathname === '/tutorials' || pathname.startsWith('/tutorials/');
}

function SidebarContent({
  navItems,
  onNavigate,
}: {
  navItems: NavItem[];
  onNavigate?: () => void;
}) {
  const location = useLocation();
  const tutorialsActive = isTutorialsPath(location.pathname);
  const [tutorialsOpen, setTutorialsOpen] = useState(tutorialsActive);

  // Open when entering / navigating within Tutorials; still allow manual collapse on the same route.
  useEffect(() => {
    if (isTutorialsPath(location.pathname)) {
      setTutorialsOpen(true);
    }
  }, [location.pathname]);

  return (
    <Box
      component="nav"
      aria-label="Primary"
      sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}
    >
      <Toolbar sx={{ px: 2.5, gap: 1.25 }}>
        <Box
          sx={{
            width: 32,
            height: 32,
            borderRadius: 1,
            bgcolor: 'primary.main',
            color: 'primary.contrastText',
            display: 'grid',
            placeItems: 'center',
            fontFamily: '"IBM Plex Mono", monospace',
            fontWeight: 600,
            fontSize: 12,
          }}
        >
          A
        </Box>
        <Box>
          <Typography variant="subtitle1" fontWeight={700} lineHeight={1.2}>
            {appConfig.name}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Career OS
          </Typography>
        </Box>
      </Toolbar>
      <Divider />
      <List sx={{ px: 1.25, py: 1.5, flex: 1, overflow: 'auto' }}>
        {navItems.map((item) => {
          if (item.path === '/tutorials') {
            return (
              <Box key={item.path}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 0.5, gap: 0.25 }}>
                  <ListItemButton
                    component={NavLink}
                    to={item.path}
                    onClick={() => {
                      setTutorialsOpen(true);
                      onNavigate?.();
                    }}
                    onMouseEnter={() => prefetchRoute(item.path)}
                    onFocus={() => prefetchRoute(item.path)}
                    sx={{
                      flex: 1,
                      minWidth: 0,
                      borderRadius: 2,
                      color: 'primary.dark',
                      '&.active': {
                        bgcolor: 'action.selected',
                        color: 'primary.main',
                        '& .MuiListItemIcon-root': { color: 'primary.main' },
                      },
                    }}
                  >
                    <ListItemIcon sx={{ minWidth: 40, color: 'primary.main' }}>{item.icon}</ListItemIcon>
                    <ListItemText
                      primary={item.label}
                      primaryTypographyProps={{ fontWeight: 700 }}
                    />
                  </ListItemButton>
                  <IconButton
                    size="small"
                    aria-label={tutorialsOpen ? 'Collapse Tutorials' : 'Expand Tutorials'}
                    aria-expanded={tutorialsOpen}
                    onClick={() => setTutorialsOpen((value) => !value)}
                    sx={{ color: 'primary.main', flexShrink: 0 }}
                  >
                    {tutorialsOpen ? <ExpandLessIcon fontSize="small" /> : <ExpandMoreIcon fontSize="small" />}
                  </IconButton>
                </Box>
                <Collapse in={tutorialsOpen} timeout="auto" unmountOnExit>
                  <TutorialSidebarTree onNavigate={onNavigate} />
                </Collapse>
              </Box>
            );
          }

          return (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              end={item.path === '/'}
              onClick={onNavigate}
              onMouseEnter={() => prefetchRoute(item.path)}
              onFocus={() => prefetchRoute(item.path)}
              sx={{
                borderRadius: 2,
                mb: 0.5,
                '&.active': {
                  bgcolor: 'action.selected',
                  color: 'primary.main',
                  '& .MuiListItemIcon-root': { color: 'primary.main' },
                },
              }}
            >
              <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          );
        })}
      </List>
    </Box>
  );
}

export function Sidebar({ mobileOpen, onMobileClose, navItems = defaultNavItems }: SidebarProps) {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up('md'));
  const width = appConfig.layout.drawerWidth;

  if (isDesktop) {
    return (
      <Drawer
        variant="permanent"
        open
        sx={{
          width,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: {
            width,
            boxSizing: 'border-box',
            bgcolor: 'background.paper',
          },
        }}
      >
        <SidebarContent navItems={navItems} />
      </Drawer>
    );
  }

  return (
    <Drawer
      variant="temporary"
      open={mobileOpen}
      onClose={onMobileClose}
      ModalProps={{ keepMounted: true }}
      sx={{
        [`& .MuiDrawer-paper`]: {
          width,
          boxSizing: 'border-box',
        },
      }}
    >
      <SidebarContent navItems={navItems} onNavigate={onMobileClose} />
    </Drawer>
  );
}
