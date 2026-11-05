import { useEffect, useState } from 'react';
import {
  Box,
  Collapse,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Typography,
} from '@mui/material';
import type { SxProps, Theme } from '@mui/material/styles';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { NavLink, useLocation } from 'react-router-dom';
import type { TutorialTreeNode } from '../types/tutorial.types';
import { useTutorialTreeQuery } from '../hooks/useTutorialQueries';

function topicTargetPath(node: TutorialTreeNode): string {
  const hubPath = `/tutorials/${node.path}`;
  return node.children.length === 0 ? `${hubPath}/concept` : hubPath;
}

function isUnderPath(pathname: string, hubPath: string): boolean {
  return pathname === hubPath || pathname.startsWith(`${hubPath}/`);
}

/** Distinct visual treatment per tree depth (level 0 = top categories). */
function levelItemSx(depth: number): SxProps<Theme> {
  if (depth === 0) {
    return {
      py: 0.75,
      color: 'primary.main',
      '& .MuiListItemText-primary': {
        color: 'primary.main',
        fontWeight: 700,
      },
      '&.active, &.Mui-selected': {
        bgcolor: 'rgba(11, 61, 46, 0.10)',
        color: 'primary.dark',
        '& .MuiListItemText-primary': { color: 'primary.dark' },
      },
    };
  }

  if (depth === 1) {
    return {
      ml: 0.75,
      py: 0.45,
      pl: 0.5,
      borderLeft: '3px solid',
      borderColor: 'secondary.main',
      bgcolor: 'rgba(196, 163, 90, 0.10)',
      borderRadius: 1.5,
      '& .MuiListItemText-primary': {
        color: 'secondary.dark',
        fontWeight: 600,
      },
      '&.active, &.Mui-selected': {
        bgcolor: 'rgba(196, 163, 90, 0.22)',
        color: 'secondary.dark',
      },
    };
  }

  return {
    ml: 1.25,
    py: 0.35,
    pl: 0.5,
    borderLeft: '3px solid',
    borderColor: 'info.main',
    bgcolor: 'rgba(23, 92, 211, 0.06)',
    borderRadius: 1.5,
    '& .MuiListItemText-primary': {
      color: 'info.main',
      fontWeight: 500,
    },
    '&.active, &.Mui-selected': {
      bgcolor: 'rgba(23, 92, 211, 0.14)',
      color: 'info.dark',
    },
  };
}

function leafLinkSx(kind: 'concept' | 'questions'): SxProps<Theme> {
  const isConcept = kind === 'concept';
  return {
    ml: 1.25,
    mb: 0.25,
    py: 0.35,
    pl: 0.5,
    borderRadius: 1.5,
    borderLeft: '3px solid',
    borderColor: isConcept ? 'success.main' : 'warning.main',
    bgcolor: isConcept ? 'rgba(6, 118, 71, 0.06)' : 'rgba(181, 71, 8, 0.06)',
    '& .MuiListItemText-primary': {
      color: isConcept ? 'success.main' : 'warning.main',
      fontWeight: 500,
    },
    '&.active, &.Mui-selected': {
      bgcolor: isConcept ? 'rgba(6, 118, 71, 0.14)' : 'rgba(181, 71, 8, 0.14)',
    },
  };
}

function TutorialTreeItem({
  node,
  depth,
  onNavigate,
}: {
  node: TutorialTreeNode;
  depth: number;
  onNavigate?: () => void;
}) {
  const location = useLocation();
  const hasChildren = node.children.length > 0;
  const hubPath = `/tutorials/${node.path}`;
  const targetPath = topicTargetPath(node);
  const conceptPath = `${hubPath}/concept`;
  const questionsPath = `${hubPath}/questions`;
  const underThisNode = isUnderPath(location.pathname, hubPath);
  const [open, setOpen] = useState(hasChildren && underThisNode);

  // Expand when navigating into this branch; allow manual collapse afterward.
  useEffect(() => {
    if (hasChildren && underThisNode) {
      setOpen(true);
    }
  }, [hasChildren, underThisNode, location.pathname]);

  const isLeafActive =
    location.pathname === conceptPath || location.pathname === hubPath;
  const isParentActive = location.pathname === hubPath;

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 0.25 }}>
        {hasChildren ? (
          <IconButton
            size="small"
            aria-label={open ? 'Collapse' : 'Expand'}
            aria-expanded={open}
            onClick={(event) => {
              event.preventDefault();
              event.stopPropagation();
              setOpen((value) => !value);
            }}
            sx={{
              mr: 0.25,
              color: depth === 0 ? 'primary.main' : depth === 1 ? 'secondary.main' : 'info.main',
            }}
          >
            {open ? <ExpandMoreIcon fontSize="small" /> : <ChevronRightIcon fontSize="small" />}
          </IconButton>
        ) : (
          <Box sx={{ width: 34, flexShrink: 0 }} />
        )}
        <ListItemButton
          component={NavLink}
          to={targetPath}
          dense
          selected={hasChildren ? isParentActive : isLeafActive}
          onClick={() => {
            if (hasChildren) {
              setOpen(true);
            }
            onNavigate?.();
          }}
          sx={{
            flex: 1,
            minWidth: 0,
            borderRadius: 1.5,
            ...levelItemSx(depth),
          }}
        >
          <ListItemText
            primary={node.title}
            primaryTypographyProps={{
              variant: depth === 0 ? 'body2' : 'caption',
            }}
          />
        </ListItemButton>
      </Box>
      {hasChildren ? (
        <Collapse in={open} timeout="auto" unmountOnExit>
          <List disablePadding sx={{ pl: 0.5 }}>
            {node.children.map((child) => (
              <TutorialTreeItem
                key={child.id}
                node={child}
                depth={depth + 1}
                onNavigate={onNavigate}
              />
            ))}
            {node.hasConcept ? (
              <ListItemButton
                component={NavLink}
                to={conceptPath}
                dense
                onClick={onNavigate}
                sx={leafLinkSx('concept')}
              >
                <Box sx={{ width: 28 }} />
                <ListItemText
                  primary="Concept"
                  primaryTypographyProps={{ variant: 'caption' }}
                />
              </ListItemButton>
            ) : null}
            {node.hasQuestions ? (
              <ListItemButton
                component={NavLink}
                to={questionsPath}
                dense
                onClick={onNavigate}
                sx={leafLinkSx('questions')}
              >
                <Box sx={{ width: 28 }} />
                <ListItemText
                  primary="Questions & Answers"
                  primaryTypographyProps={{ variant: 'caption' }}
                />
              </ListItemButton>
            ) : null}
          </List>
        </Collapse>
      ) : null}
    </Box>
  );
}

export function TutorialSidebarTree({ onNavigate }: { onNavigate?: () => void }) {
  const { data, isLoading, isError } = useTutorialTreeQuery();

  if (isLoading) {
    return (
      <Typography variant="caption" color="text.secondary" sx={{ px: 2, py: 1 }}>
        Loading tutorials…
      </Typography>
    );
  }

  if (isError) {
    return (
      <Typography variant="caption" color="error" sx={{ px: 2, py: 1 }}>
        Unable to load tutorials
      </Typography>
    );
  }

  if (!data?.length) {
    return (
      <Typography variant="caption" color="text.secondary" sx={{ px: 2, py: 1 }}>
        No tutorials yet
      </Typography>
    );
  }

  return (
    <List dense disablePadding sx={{ px: 0.5, pb: 1 }}>
      {data.map((node) => (
        <TutorialTreeItem key={node.id} node={node} depth={0} onNavigate={onNavigate} />
      ))}
    </List>
  );
}
