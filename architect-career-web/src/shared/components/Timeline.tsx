import type { ReactNode } from 'react';
import { Box, Stack, Typography } from '@mui/material';

export interface TimelineItem {
  id: string;
  title: string;
  description?: string;
  meta?: string;
  active?: boolean;
  completed?: boolean;
  icon?: ReactNode;
}

interface TimelineProps {
  items: TimelineItem[];
}

export function Timeline({ items }: TimelineProps) {
  if (items.length === 0) {
    return (
      <Typography variant="body2" color="text.secondary">
        No timeline events yet.
      </Typography>
    );
  }

  return (
    <Stack spacing={0}>
      {items.map((item, index) => (
        <Box key={item.id} sx={{ display: 'flex', gap: 2 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: 24 }}>
            <Box
              sx={{
                width: 12,
                height: 12,
                borderRadius: '50%',
                bgcolor: item.active
                  ? 'secondary.main'
                  : item.completed
                    ? 'primary.main'
                    : 'divider',
                mt: 0.5,
              }}
            />
            {index < items.length - 1 ? (
              <Box sx={{ flex: 1, width: 2, bgcolor: 'divider', my: 0.5, minHeight: 28 }} />
            ) : null}
          </Box>
          <Box sx={{ pb: 2.5, flex: 1 }}>
            <Typography variant="subtitle2">{item.title}</Typography>
            {item.meta ? (
              <Typography variant="caption" color="text.secondary" display="block">
                {item.meta}
              </Typography>
            ) : null}
            {item.description ? (
              <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                {item.description}
              </Typography>
            ) : null}
          </Box>
        </Box>
      ))}
    </Stack>
  );
}
