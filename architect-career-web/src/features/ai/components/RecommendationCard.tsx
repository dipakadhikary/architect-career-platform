import { Chip, Paper, Stack, Typography } from '@mui/material';

interface RecommendationCardProps {
  title: string;
  rationale?: string;
  related?: string[];
  score?: number | null;
}

export function RecommendationCard({
  title,
  rationale,
  related = [],
  score,
}: RecommendationCardProps) {
  return (
    <Paper variant="outlined" sx={{ p: 2.5, height: '100%' }}>
      <Stack direction="row" justifyContent="space-between" spacing={2} sx={{ mb: 1 }}>
        <Typography variant="h6">{title}</Typography>
        {score != null ? (
          <Chip size="small" color="secondary" label={`Score ${score.toFixed(1)}`} />
        ) : null}
      </Stack>
      {rationale ? (
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5 }}>
          {rationale}
        </Typography>
      ) : null}
      {related.length > 0 ? (
        <Stack direction="row" flexWrap="wrap" gap={0.75}>
          {related.map((item) => (
            <Chip key={item} size="small" label={item} variant="outlined" />
          ))}
        </Stack>
      ) : null}
    </Paper>
  );
}
