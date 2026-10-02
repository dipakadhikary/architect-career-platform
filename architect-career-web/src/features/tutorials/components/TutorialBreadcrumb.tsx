import { Breadcrumbs, Link as MuiLink, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import type { TutorialBreadcrumbItem } from '../types/tutorial.types';

export function TutorialBreadcrumb({
  items,
  leaf,
}: {
  items: TutorialBreadcrumbItem[];
  leaf: string;
}) {
  return (
    <Breadcrumbs aria-label="Tutorial breadcrumb" sx={{ mb: 2 }}>
      <MuiLink component={RouterLink} underline="hover" color="inherit" to="/tutorials">
        Tutorials
      </MuiLink>
      {items.map((item) => (
        <MuiLink
          key={item.id}
          component={RouterLink}
          underline="hover"
          color="inherit"
          to={`/tutorials/${item.path}/concept`}
        >
          {item.title}
        </MuiLink>
      ))}
      <Typography color="text.primary">{leaf}</Typography>
    </Breadcrumbs>
  );
}
