import type { ReactNode } from 'react';
import { Box, Paper, Typography } from '@mui/material';

interface AiCardProps {
  title: string;
  description?: string;
  actions?: ReactNode;
  children?: ReactNode;
}

export function AiCard({ title, description, actions, children }: AiCardProps) {
  return (
    <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', gap: 2, mb: children ? 2 : 0 }}>
        <Box>
          <Typography variant="h6">{title}</Typography>
          {description ? (
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              {description}
            </Typography>
          ) : null}
        </Box>
        {actions}
      </Box>
      {children}
    </Paper>
  );
}
