import { useState } from 'react';
import {
  Box,
  Collapse,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Typography,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { NavLink, useLocation } from 'react-router-dom';
import type { TutorialTreeNode } from '../types/tutorial.types';
import { useTutorialTreeQuery } from '../hooks/useTutorialQueries';

function topicTargetPath(node: TutorialTreeNode): string {
  const hubPath = `/tutorials/${node.path}`;
  return node.children.length === 0 ? `${hubPath}/concept` : hubPath;
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
  const [open, setOpen] = useState(false);
  const hubPath = `/tutorials/${node.path}`;
  const targetPath = topicTargetPath(node);
  const conceptPath = `${hubPath}/concept`;
  const questionsPath = `${hubPath}/questions`;
  const isLeafActive =
    location.pathname === conceptPath || location.pathname === hubPath;
  const isParentActive = location.pathname === hubPath;

  const childStyle = {
    borderRadius: 1.5,
    mb: 0.25,
    ml: 0.5,
    borderLeft: '2px solid',
    borderColor: 'divider',
    bgcolor: 'action.hover',
    py: 0.4,
    '&.active, &.Mui-selected': {
      bgcolor: 'action.selected',
      color: 'primary.main',
    },
  } as const;

  return (
    <Box>
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
          borderRadius: 1.5,
          mb: 0.25,
          ...(depth === 0
            ? { py: 0.75 }
            : {
                ml: 0.5,
                borderLeft: '2px solid',
                borderColor: 'divider',
                bgcolor: 'action.hover',
                py: 0.4,
              }),
          '&.active, &.Mui-selected': {
            bgcolor: 'action.selected',
            color: 'primary.main',
          },
        }}
      >
        {hasChildren ? (
          <IconButton
            size="small"
            aria-label={open ? 'Collapse' : 'Expand'}
            onClick={(event) => {
              event.preventDefault();
              event.stopPropagation();
              setOpen((value) => !value);
            }}
            sx={{ mr: 0.5 }}
          >
            {open ? <ExpandMoreIcon fontSize="small" /> : <ChevronRightIcon fontSize="small" />}
          </IconButton>
        ) : (
          <Box sx={{ width: 28 }} />
        )}
        <ListItemText
          primary={node.title}
          primaryTypographyProps={{
            variant: depth === 0 ? 'body2' : 'caption',
            fontWeight: depth === 0 ? 600 : 500,
            color: depth === 0 ? 'text.primary' : 'text.secondary',
          }}
        />
      </ListItemButton>
      {hasChildren ? (
        <Collapse in={open} timeout="auto" unmountOnExit>
          <List disablePadding>
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
                sx={{
                  ...childStyle,
                  borderColor: 'primary.light',
                }}
              >
                <Box sx={{ width: 28 }} />
                <ListItemText
                  primary="Concept"
                  primaryTypographyProps={{ variant: 'caption', fontWeight: 500 }}
                />
              </ListItemButton>
            ) : null}
            {node.hasQuestions ? (
              <ListItemButton
                component={NavLink}
                to={questionsPath}
                dense
                onClick={onNavigate}
                sx={{
                  ...childStyle,
                  borderColor: 'secondary.light',
                }}
              >
                <Box sx={{ width: 28 }} />
                <ListItemText
                  primary="Questions & Answers"
                  primaryTypographyProps={{ variant: 'caption', fontWeight: 500 }}
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
