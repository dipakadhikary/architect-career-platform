import { Button, Paper, Typography } from '@mui/material';

interface SuggestionCardProps {
  title: string;
  description: string;
  actionLabel?: string;
  onAction?: () => void;
  disabled?: boolean;
}

export function SuggestionCard({
  title,
  description,
  actionLabel = 'Use',
  onAction,
  disabled = false,
}: SuggestionCardProps) {
  return (
    <Paper variant="outlined" sx={{ p: 2, height: '100%' }}>
      <Typography variant="subtitle2" gutterBottom>
        {title}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5 }}>
        {description}
      </Typography>
      {onAction ? (
        <Button size="small" onClick={onAction} disabled={disabled}>
          {actionLabel}
        </Button>
      ) : null}
    </Paper>
  );
}
