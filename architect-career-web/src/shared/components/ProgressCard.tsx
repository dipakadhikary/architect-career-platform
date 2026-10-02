import { Box, LinearProgress, Paper, Typography } from '@mui/material';

interface ProgressCardProps {
  title: string;
  progress: number;
  subtitle?: string;
  detail?: string;
}

export function ProgressCard({ title, progress, subtitle, detail }: ProgressCardProps) {
  const clamped = Math.max(0, Math.min(100, progress));

  return (
    <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
      <Typography variant="subtitle1" fontWeight={600}>
        {title}
      </Typography>
      {subtitle ? (
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
          {subtitle}
        </Typography>
      ) : null}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 2 }}>
        <LinearProgress
          variant="determinate"
          value={clamped}
          sx={{ flex: 1, height: 8, borderRadius: 999 }}
        />
        <Typography variant="body2" fontWeight={600} sx={{ minWidth: 40, textAlign: 'right' }}>
          {clamped}%
        </Typography>
      </Box>
      {detail ? (
        <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
          {detail}
        </Typography>
      ) : null}
    </Paper>
  );
}
