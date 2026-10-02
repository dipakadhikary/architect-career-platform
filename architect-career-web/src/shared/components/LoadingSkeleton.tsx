import { Box, Skeleton, Stack } from '@mui/material';

interface LoadingSkeletonProps {
  variant?: 'page' | 'cards' | 'table' | 'chat';
}

export function LoadingSkeleton({ variant = 'page' }: LoadingSkeletonProps) {
  if (variant === 'cards') {
    return (
      <Box
        sx={{
          display: 'grid',
          gap: 2,
          gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(3, 1fr)' },
        }}
      >
        {Array.from({ length: 6 }).map((_, index) => (
          <Skeleton key={index} variant="rounded" height={120} />
        ))}
      </Box>
    );
  }

  if (variant === 'table') {
    return (
      <Stack spacing={1}>
        <Skeleton variant="rounded" height={48} />
        {Array.from({ length: 5 }).map((_, index) => (
          <Skeleton key={index} variant="rounded" height={40} />
        ))}
      </Stack>
    );
  }

  if (variant === 'chat') {
    return (
      <Stack spacing={2}>
        <Skeleton variant="rounded" height={72} width="70%" />
        <Skeleton variant="rounded" height={72} width="55%" sx={{ alignSelf: 'flex-end' }} />
        <Skeleton variant="rounded" height={96} width="65%" />
      </Stack>
    );
  }

  return (
    <Stack spacing={2} sx={{ py: 2 }}>
      <Skeleton variant="text" width="40%" height={40} />
      <Skeleton variant="text" width="70%" />
      <Skeleton variant="rounded" height={180} />
      <Box
        sx={{
          display: 'grid',
          gap: 2,
          gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
        }}
      >
        <Skeleton variant="rounded" height={120} />
        <Skeleton variant="rounded" height={120} />
      </Box>
    </Stack>
  );
}
