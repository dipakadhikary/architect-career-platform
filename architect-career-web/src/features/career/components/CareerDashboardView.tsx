import { Box, Grid, Typography } from '@mui/material';
import BusinessOutlinedIcon from '@mui/icons-material/BusinessOutlined';
import PeopleOutlineIcon from '@mui/icons-material/PeopleOutline';
import AssignmentOutlinedIcon from '@mui/icons-material/AssignmentOutlined';
import EventOutlinedIcon from '@mui/icons-material/EventOutlined';
import LocalOfferOutlinedIcon from '@mui/icons-material/LocalOfferOutlined';
import TrendingUpOutlinedIcon from '@mui/icons-material/TrendingUpOutlined';
import StarOutlineIcon from '@mui/icons-material/Grade';
import { ErrorPanel, LoadingSpinner, StatisticsCard, StatusChip } from '@/shared/components';
import { useCareerDashboard } from '@/features/career/hooks/career.hooks';
import { getErrorMessage } from '@/shared/utils/error';
import { formatEnumLabel } from '@/shared/utils/label';

export function CareerDashboardView() {
  const { data, isLoading, isError, error, refetch } = useCareerDashboard();

  if (isLoading) {
    return <LoadingSpinner label="Loading dashboard…" />;
  }

  if (isError || !data) {
    return (
      <ErrorPanel
        message={getErrorMessage(error, 'Failed to load career dashboard')}
        onRetry={() => void refetch()}
      />
    );
  }

  const acceptancePercent = `${(data.acceptanceRatio * 100).toFixed(1)}%`;

  return (
    <Box>
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard
            label="Companies"
            value={data.totalCompanies}
            icon={<BusinessOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard
            label="Recruiters"
            value={data.totalRecruiters}
            icon={<PeopleOutlineIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard
            label="Active Applications"
            value={data.totalActiveApplications}
            icon={<AssignmentOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard
            label="Interviews Scheduled"
            value={data.interviewsScheduled}
            helperText={`${data.upcomingInterviews} upcoming`}
            icon={<EventOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard
            label="Offers Received"
            value={data.offersReceived}
            helperText={`${data.pendingOffers} pending · ${data.acceptedOffers} accepted`}
            icon={<LocalOfferOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard
            label="Acceptance Ratio"
            value={acceptancePercent}
            icon={<TrendingUpOutlinedIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard
            label="Avg Interview Rating"
            value={
              data.averageInterviewRating != null ? data.averageInterviewRating.toFixed(1) : '—'
            }
            icon={<StarOutlineIcon />}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <StatisticsCard label="Rejected Applications" value={data.rejectedApplications} />
        </Grid>
      </Grid>

      <Typography variant="h6" gutterBottom>
        Applications by Status
      </Typography>
      <Grid container spacing={1.5}>
        {data.applicationsByStatus.length === 0 ? (
          <Grid size={12}>
            <Typography variant="body2" color="text.secondary">
              No application data yet.
            </Typography>
          </Grid>
        ) : (
          data.applicationsByStatus.map((item) => (
            <Grid key={item.status} size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
              <StatisticsCard
                label={formatEnumLabel(item.status)}
                value={item.count}
                icon={<StatusChip status={item.status} />}
              />
            </Grid>
          ))
        )}
      </Grid>
    </Box>
  );
}
