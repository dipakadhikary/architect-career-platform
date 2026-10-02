import { useState } from 'react';
import { Box, Button, Divider, Grid, Paper, Stack, Tab, Tabs, Typography } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import SwapHorizIcon from '@mui/icons-material/CompareArrows';
import ArchiveOutlinedIcon from '@mui/icons-material/ArchiveOutlined';
import { useNavigate } from 'react-router-dom';
import {
  ConfirmationDialog,
  ErrorPanel,
  LoadingSpinner,
  StatusChip,
  Timeline,
} from '@/shared/components';
import { ApplicationFormDialog } from '@/features/career/components/ApplicationFormDialog';
import { InterviewsSection } from '@/features/career/components/InterviewsSection';
import { OffersSection } from '@/features/career/components/OffersSection';
import { StatusTransitionDialog } from '@/features/career/components/StatusTransitionDialog';
import {
  useApplication,
  useApplicationHistory,
  useApplicationMutations,
  useApplicationTimeline,
  useCompanies,
  useRecruiters,
} from '@/features/career/hooks/career.hooks';
import type { ApplicationFormValues } from '@/features/career/schemas/career.schemas';
import type { StatusTransitionFormValues } from '@/features/career/schemas/career.schemas';
import {
  getAllowedNextStatuses,
  isTerminalApplicationStatus,
} from '@/features/career/utils/applicationStatus';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { formatDate, formatDateTime } from '@/shared/utils/date';
import { formatEnumLabel } from '@/shared/utils/label';

interface ApplicationDetailViewProps {
  applicationId: string;
}

function mapApplicationFormToRequest(values: ApplicationFormValues) {
  return {
    companyId: values.companyId,
    recruiterId: values.recruiterId || undefined,
    title: values.title,
    jobDescription: values.jobDescription || undefined,
    source: values.source || undefined,
    salaryExpectation:
      values.salaryExpectation === '' ? undefined : Number(values.salaryExpectation),
    currency: values.currency || undefined,
    resumeVersion: values.resumeVersion || undefined,
    appliedOn: values.appliedOn,
    location: values.location || undefined,
    jobUrl: values.jobUrl || undefined,
    notes: values.notes || undefined,
  };
}

export function ApplicationDetailView({ applicationId }: ApplicationDetailViewProps) {
  const navigate = useNavigate();
  const notification = useNotification();
  const [tab, setTab] = useState(0);
  const [editOpen, setEditOpen] = useState(false);
  const [statusOpen, setStatusOpen] = useState(false);
  const [archiveOpen, setArchiveOpen] = useState(false);

  const applicationQuery = useApplication(applicationId);
  const historyQuery = useApplicationHistory(applicationId);
  const timelineQuery = useApplicationTimeline(applicationId);
  const companiesQuery = useCompanies();
  const recruitersQuery = useRecruiters();
  const { update, updateStatus, archive } = useApplicationMutations();

  const application = applicationQuery.data;

  const handleEdit = async (values: ApplicationFormValues) => {
    if (!application) return;
    try {
      await update.mutateAsync({
        id: application.id,
        payload: mapApplicationFormToRequest(values),
      });
      notification.success('Application updated');
      setEditOpen(false);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to update application'));
    }
  };

  const handleStatusChange = async (values: StatusTransitionFormValues) => {
    if (!application) return;
    try {
      await updateStatus.mutateAsync({
        id: application.id,
        payload: {
          newStatus: values.newStatus,
          comments: values.comments || undefined,
        },
      });
      notification.success('Status updated');
      setStatusOpen(false);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to update status'));
    }
  };

  const handleArchive = async () => {
    if (!application) return;
    try {
      await archive.mutateAsync(application.id);
      notification.success('Application archived');
      navigate('/career/applications');
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to archive application'));
    }
  };

  if (applicationQuery.isLoading) {
    return <LoadingSpinner label="Loading application…" />;
  }

  if (applicationQuery.isError || !application) {
    return (
      <ErrorPanel
        message={getErrorMessage(applicationQuery.error, 'Failed to load application')}
        onRetry={() => void applicationQuery.refetch()}
      />
    );
  }

  const allowedStatuses = getAllowedNextStatuses(application.status);
  const canTransition = allowedStatuses.length > 0;

  const timelineItems =
    timelineQuery.data?.steps.map((step) => ({
      id: step.status,
      title: step.label || formatEnumLabel(step.status),
      meta: step.changedAt ? formatDateTime(step.changedAt) : undefined,
      active: step.current,
      completed: step.reached && !step.current,
    })) ?? [];

  return (
    <Box>
      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 2 }}>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/career/applications')}>
          Back
        </Button>
      </Stack>

      <Paper variant="outlined" sx={{ p: 3, mb: 3 }}>
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          justifyContent="space-between"
          alignItems={{ xs: 'stretch', sm: 'flex-start' }}
          spacing={2}
        >
          <Box>
            <Typography variant="h5" gutterBottom>
              {application.title}
            </Typography>
            <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
              <StatusChip status={application.status} size="medium" />
              <Typography variant="body2" color="text.secondary">
                {application.company.name}
              </Typography>
              {application.recruiter ? (
                <Typography variant="body2" color="text.secondary">
                  · {application.recruiter.fullName}
                </Typography>
              ) : null}
            </Stack>
          </Box>
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            <Button
              variant="outlined"
              startIcon={<EditOutlinedIcon />}
              onClick={() => setEditOpen(true)}
            >
              Edit
            </Button>
            {canTransition ? (
              <Button
                variant="outlined"
                startIcon={<SwapHorizIcon />}
                onClick={() => setStatusOpen(true)}
              >
                Update Status
              </Button>
            ) : null}
            {!application.archived && !isTerminalApplicationStatus(application.status) ? (
              <Button
                variant="outlined"
                color="warning"
                startIcon={<ArchiveOutlinedIcon />}
                onClick={() => setArchiveOpen(true)}
              >
                Archive
              </Button>
            ) : null}
          </Stack>
        </Stack>

        <Divider sx={{ my: 2 }} />

        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography variant="caption" color="text.secondary">
              Applied On
            </Typography>
            <Typography variant="body2">{formatDate(application.appliedOn)}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography variant="caption" color="text.secondary">
              Location
            </Typography>
            <Typography variant="body2">{application.location ?? '—'}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography variant="caption" color="text.secondary">
              Source
            </Typography>
            <Typography variant="body2">{application.source ?? '—'}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography variant="caption" color="text.secondary">
              Salary Expectation
            </Typography>
            <Typography variant="body2">
              {application.salaryExpectation != null
                ? `${application.salaryExpectation} ${application.currency ?? ''}`.trim()
                : '—'}
            </Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography variant="caption" color="text.secondary">
              Resume Version
            </Typography>
            <Typography variant="body2">{application.resumeVersion ?? '—'}</Typography>
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography variant="caption" color="text.secondary">
              Job URL
            </Typography>
            <Typography variant="body2">
              {application.jobUrl ? (
                <a href={application.jobUrl} target="_blank" rel="noreferrer">
                  {application.jobUrl}
                </a>
              ) : (
                '—'
              )}
            </Typography>
          </Grid>
          {application.jobDescription ? (
            <Grid size={12}>
              <Typography variant="caption" color="text.secondary">
                Job Description
              </Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                {application.jobDescription}
              </Typography>
            </Grid>
          ) : null}
          {application.notes ? (
            <Grid size={12}>
              <Typography variant="caption" color="text.secondary">
                Notes
              </Typography>
              <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                {application.notes}
              </Typography>
            </Grid>
          ) : null}
        </Grid>
      </Paper>

      <Tabs value={tab} onChange={(_, value) => setTab(value)} sx={{ mb: 2 }}>
        <Tab label="Timeline" />
        <Tab label="Status History" />
        <Tab label="Interviews" />
        <Tab label="Offers" />
      </Tabs>

      {tab === 0 ? (
        timelineQuery.isLoading ? (
          <LoadingSpinner label="Loading timeline…" />
        ) : timelineQuery.isError ? (
          <ErrorPanel
            message={getErrorMessage(timelineQuery.error, 'Failed to load timeline')}
            onRetry={() => void timelineQuery.refetch()}
          />
        ) : (
          <Timeline items={timelineItems} />
        )
      ) : null}

      {tab === 1 ? (
        historyQuery.isLoading ? (
          <LoadingSpinner label="Loading history…" />
        ) : historyQuery.isError ? (
          <ErrorPanel
            message={getErrorMessage(historyQuery.error, 'Failed to load history')}
            onRetry={() => void historyQuery.refetch()}
          />
        ) : (historyQuery.data ?? []).length === 0 ? (
          <Typography variant="body2" color="text.secondary">
            No status changes recorded yet.
          </Typography>
        ) : (
          <Timeline
            items={(historyQuery.data ?? []).map((entry) => ({
              id: entry.id,
              title: `${entry.previousStatus ? formatEnumLabel(entry.previousStatus) : 'Initial'} → ${formatEnumLabel(entry.newStatus)}`,
              meta: formatDateTime(entry.changedAt),
              description: entry.comments ?? undefined,
              completed: true,
            }))}
          />
        )
      ) : null}

      {tab === 2 ? <InterviewsSection applicationId={applicationId} /> : null}
      {tab === 3 ? <OffersSection applicationId={applicationId} /> : null}

      <ApplicationFormDialog
        open={editOpen}
        application={application}
        companies={companiesQuery.data ?? []}
        recruiters={recruitersQuery.data ?? []}
        loading={update.isPending}
        onClose={() => setEditOpen(false)}
        onSubmit={(values) => void handleEdit(values)}
      />

      <StatusTransitionDialog
        open={statusOpen}
        currentStatus={application.status}
        allowedStatuses={allowedStatuses}
        loading={updateStatus.isPending}
        onClose={() => setStatusOpen(false)}
        onSubmit={(values) => void handleStatusChange(values)}
      />

      <ConfirmationDialog
        open={archiveOpen}
        title="Archive Application"
        description="This application will be archived and removed from active lists."
        confirmLabel="Archive"
        danger
        loading={archive.isPending}
        onConfirm={() => void handleArchive()}
        onCancel={() => setArchiveOpen(false)}
      />
    </Box>
  );
}
