import { Paper, Typography } from '@mui/material';
import { EmptyState, ErrorPanel, LoadingOverlay, Timeline } from '@/shared/components';
import { getErrorMessage } from '@/shared/utils/error';
import { useExperienceTimeline } from '../hooks/useExperienceTimeline';

export function ExperienceTab() {
  const { items, isLoading, isError, error, refetch } = useExperienceTimeline();

  if (isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(error, 'Unable to load experience timeline')}
        onRetry={refetch}
      />
    );
  }

  if (isLoading) {
    return <LoadingOverlay open />;
  }

  if (items.length === 0) {
    return (
      <EmptyState
        title="No experience events yet"
        description="Add projects with dates, certifications, or achievements to build your career timeline."
      />
    );
  }

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        Career timeline
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Composed from projects, certifications, and achievements — sorted chronologically.
      </Typography>
      <Timeline items={items} />
    </Paper>
  );
}
