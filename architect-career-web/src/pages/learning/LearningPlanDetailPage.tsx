import AddIcon from '@mui/icons-material/Add';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { Box, Button, Grid, Stack, Typography } from '@mui/material';
import { useMemo, useRef, useState } from 'react';
import { Link as RouterLink, useParams } from 'react-router-dom';
import {
  ConfirmationDialog,
  EmptyState,
  ErrorPanel,
  FormDialog,
  LoadingOverlay,
  PageHeader,
  ProgressCard,
  StatisticsCard,
  StatusChip,
  Timeline,
  type TimelineItem,
} from '@/shared/components';
import { formatDate } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import {
  MilestoneForm,
  MilestoneSection,
  toMilestoneFormValues,
  toMilestoneRequest,
  type MilestoneFormHandle,
} from '@/features/learning/components';
import {
  useCreateMilestone,
  useDeleteMilestone,
  useLearningPlan,
  useUpdateMilestone,
} from '@/features/learning/hooks';
import type { LearningMilestoneResponse } from '@/features/learning/types/learning.types';
import type { LearningMilestoneFormValues } from '@/features/learning/schemas/learning.schemas';

function sortMilestones(milestones: LearningMilestoneResponse[]): LearningMilestoneResponse[] {
  return [...milestones].sort((a, b) => {
    if (a.sortOrder !== b.sortOrder) return a.sortOrder - b.sortOrder;
    if (a.targetDate && b.targetDate) {
      return new Date(a.targetDate).getTime() - new Date(b.targetDate).getTime();
    }
    if (a.targetDate) return -1;
    if (b.targetDate) return 1;
    return a.title.localeCompare(b.title);
  });
}

export function LearningPlanDetailPage() {
  const { planId } = useParams<{ planId: string }>();

  const [milestoneDialogOpen, setMilestoneDialogOpen] = useState(false);
  const [editingMilestone, setEditingMilestone] = useState<LearningMilestoneResponse | null>(null);
  const [deleteMilestone, setDeleteMilestone] = useState<LearningMilestoneResponse | null>(null);

  const milestoneFormRef = useRef<MilestoneFormHandle>(null);

  const { data: plan, isLoading, isError, error, refetch } = useLearningPlan(planId);
  const createMilestone = useCreateMilestone(planId ?? '');
  const updateMilestone = useUpdateMilestone(planId ?? '');
  const removeMilestone = useDeleteMilestone(planId ?? '');

  const sortedMilestones = useMemo(
    () => sortMilestones(plan?.milestones ?? []),
    [plan?.milestones],
  );

  const timelineItems: TimelineItem[] = useMemo(
    () =>
      sortedMilestones.map((milestone) => ({
        id: milestone.id,
        title: milestone.title,
        description: milestone.description ?? undefined,
        meta: [
          milestone.targetDate ? `Target: ${formatDate(milestone.targetDate)}` : null,
          `Progress: ${milestone.progressPercent}%`,
          `Topics: ${milestone.completedTopics}/${milestone.totalTopics}`,
        ]
          .filter(Boolean)
          .join(' · '),
        completed: milestone.progressPercent >= 100,
        active:
          milestone.progressPercent > 0 &&
          milestone.progressPercent < 100 &&
          milestone.topics.some((topic) => topic.status === 'IN_PROGRESS'),
      })),
    [sortedMilestones],
  );

  const handleMilestoneSubmit = (values: LearningMilestoneFormValues) => {
    const payload = toMilestoneRequest(values);
    if (editingMilestone) {
      updateMilestone.mutate(
        { milestoneId: editingMilestone.id, payload },
        {
          onSuccess: () => {
            setMilestoneDialogOpen(false);
            setEditingMilestone(null);
          },
        },
      );
      return;
    }
    createMilestone.mutate(payload, {
      onSuccess: () => setMilestoneDialogOpen(false),
    });
  };

  if (isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(error, 'Failed to load learning plan')}
        onRetry={() => void refetch()}
      />
    );
  }

  if (isLoading || !plan) {
    return <LoadingOverlay open label="Loading learning plan…" />;
  }

  return (
    <>
      <PageHeader
        title={plan.title}
        description={plan.description ?? undefined}
        actions={
          <Stack direction="row" spacing={1}>
            <Button
              component={RouterLink}
              to="/learning"
              startIcon={<ArrowBackIcon />}
              color="inherit"
            >
              All plans
            </Button>
            <Button
              variant="outlined"
              startIcon={<AddIcon />}
              onClick={() => {
                setEditingMilestone(null);
                setMilestoneDialogOpen(true);
              }}
            >
              Add milestone
            </Button>
          </Stack>
        }
      />

      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 3 }}>
        <StatusChip status={plan.status} size="medium" />
        {plan.targetDate ? (
          <Typography variant="body2" color="text.secondary">
            Target date: {formatDate(plan.targetDate)}
          </Typography>
        ) : null}
      </Stack>

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, md: 4 }}>
          <ProgressCard
            title="Overall progress"
            progress={plan.progressPercent}
            subtitle={`${plan.completedTopics} of ${plan.totalTopics} topics completed`}
            detail={`Last updated ${formatDate(plan.updatedAt)}`}
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <StatisticsCard
            label="Topics completed"
            value={plan.completedTopics}
            helperText="Across all milestones"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4 }}>
          <StatisticsCard
            label="Total topics"
            value={plan.totalTopics}
            helperText={`${sortedMilestones.length} milestone${sortedMilestones.length === 1 ? '' : 's'}`}
          />
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, lg: 4 }}>
          <Box sx={{ mb: 2 }}>
            <Typography variant="h6" gutterBottom>
              Milestone timeline
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Ordered by sort order and target date.
            </Typography>
          </Box>
          {timelineItems.length === 0 ? (
            <EmptyState
              title="No milestones yet"
              description="Add milestones to structure your learning plan."
              actionLabel="Add milestone"
              onAction={() => {
                setEditingMilestone(null);
                setMilestoneDialogOpen(true);
              }}
            />
          ) : (
            <Timeline items={timelineItems} />
          )}
        </Grid>

        <Grid size={{ xs: 12, lg: 8 }}>
          <Stack spacing={2}>
            {sortedMilestones.length === 0
              ? null
              : sortedMilestones.map((milestone) => (
                  <MilestoneSection
                    key={milestone.id}
                    planId={plan.id}
                    milestone={milestone}
                    onEditMilestone={(item) => {
                      setEditingMilestone(item);
                      setMilestoneDialogOpen(true);
                    }}
                    onDeleteMilestone={setDeleteMilestone}
                  />
                ))}
          </Stack>
        </Grid>
      </Grid>

      <FormDialog
        open={milestoneDialogOpen}
        title={editingMilestone ? 'Edit milestone' : 'Add milestone'}
        submitLabel={editingMilestone ? 'Save changes' : 'Create milestone'}
        loading={createMilestone.isPending || updateMilestone.isPending}
        onClose={() => {
          setMilestoneDialogOpen(false);
          setEditingMilestone(null);
        }}
        onSubmit={() => milestoneFormRef.current?.submit()}
      >
        <MilestoneForm
          ref={milestoneFormRef}
          defaultValues={
            editingMilestone
              ? toMilestoneFormValues(editingMilestone)
              : { sortOrder: sortedMilestones.length }
          }
          onSubmit={handleMilestoneSubmit}
        />
      </FormDialog>

      <ConfirmationDialog
        open={Boolean(deleteMilestone)}
        title="Delete milestone"
        description={`Delete "${deleteMilestone?.title}" and all of its topics?`}
        confirmLabel="Delete"
        danger
        loading={removeMilestone.isPending}
        onCancel={() => setDeleteMilestone(null)}
        onConfirm={() => {
          if (deleteMilestone) {
            removeMilestone.mutate(deleteMilestone.id, {
              onSuccess: () => setDeleteMilestone(null),
            });
          }
        }}
      />
    </>
  );
}
