import { Box, CircularProgress, Stack, Typography } from '@mui/material';

interface AiLoadingIndicatorProps {
  label?: string;
  compact?: boolean;
}

export function AiLoadingIndicator({
  label = 'Generating…',
  compact = false,
}: AiLoadingIndicatorProps) {
  return (
    <Stack
      direction={compact ? 'row' : 'column'}
      spacing={compact ? 1.5 : 2}
      alignItems="center"
      justifyContent="center"
      sx={{ py: compact ? 1 : 4 }}
      role="status"
      aria-live="polite"
      aria-busy="true"
    >
      <CircularProgress size={compact ? 20 : 36} />
      <Typography variant="body2" color="text.secondary">
        {label}
      </Typography>
      {!compact ? (
        <Box
          sx={{
            display: 'flex',
            gap: 0.75,
            '& span': {
              width: 8,
              height: 8,
              borderRadius: '50%',
              bgcolor: 'primary.main',
              animation: 'acos-ai-pulse 1.2s ease-in-out infinite',
            },
            '& span:nth-of-type(2)': { animationDelay: '0.2s' },
            '& span:nth-of-type(3)': { animationDelay: '0.4s' },
            '@keyframes acos-ai-pulse': {
              '0%, 80%, 100%': { opacity: 0.3, transform: 'scale(0.85)' },
              '40%': { opacity: 1, transform: 'scale(1)' },
            },
          }}
        >
          <span />
          <span />
          <span />
        </Box>
      ) : null}
    </Stack>
  );
}
