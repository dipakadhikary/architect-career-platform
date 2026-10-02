import type { ReactNode } from 'react';
import { Box, Paper, Typography } from '@mui/material';

interface StatisticsCardProps {
  label: string;
  value: string | number;
  helperText?: string;
  icon?: ReactNode;
  onClick?: () => void;
}

export function StatisticsCard({ label, value, helperText, icon, onClick }: StatisticsCardProps) {
  return (
    <Paper
      variant="outlined"
      onClick={onClick}
      sx={{
        p: 2.5,
        height: '100%',
        cursor: onClick ? 'pointer' : 'default',
        transition: 'border-color 0.2s ease, transform 0.2s ease',
        '&:hover': onClick
          ? {
              borderColor: 'primary.main',
              transform: 'translateY(-2px)',
            }
          : undefined,
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', gap: 2 }}>
        <Box>
          <Typography variant="overline" color="text.secondary">
            {label}
          </Typography>
          <Typography variant="h4" component="p" sx={{ mt: 0.5, mb: 0.5 }}>
            {value}
          </Typography>
          {helperText ? (
            <Typography variant="body2" color="text.secondary">
              {helperText}
            </Typography>
          ) : null}
        </Box>
        {icon ? <Box sx={{ color: 'primary.main', opacity: 0.85 }}>{icon}</Box> : null}
      </Box>
    </Paper>
  );
}
