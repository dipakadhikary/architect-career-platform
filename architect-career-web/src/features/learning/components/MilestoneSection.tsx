import {
  Box,
  Button,
  IconButton,
  LinearProgress,
  MenuItem,
  Paper,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { useMemo, useRef, useState } from 'react';
import { ConfirmationDialog, EmptyState, FormDialog, StatusChip } from '@/shared/components';
import { formatDate } from '@/shared/utils/date';
import { formatEnumLabel } from '@/shared/utils/label';
import { TopicForm, toTopicFormValues, toTopicRequest, type TopicFormHandle } from './TopicForm';
import { useCreateTopic, useDeleteTopic, useUpdateTopic, useUpdateTopicStatus } from '../hooks';
import type { LearningMilestoneResponse, TopicStatus } from '../types/learning.types';
import type { LearningTopicFormValues } from '../schemas/learning.schemas';
import { TOPIC_STATUSES } from '../types/learning.types';

interface MilestoneSectionProps {
  planId: string;
  milestone: LearningMilestoneResponse;
  onEditMilestone: (milestone: LearningMilestoneResponse) => void;
  onDeleteMilestone: (milestone: LearningMilestoneResponse) => void;
}

export function MilestoneSection({
  planId,
  milestone,
  onEditMilestone,
  onDeleteMilestone,
}: MilestoneSectionProps) {
  const [topicDialogOpen, setTopicDialogOpen] = useState(false);
  const [editingTopicId, setEditingTopicId] = useState<string | null>(null);
  const [deleteTopicId, setDeleteTopicId] = useState<string | null>(null);

  const createTopic = useCreateTopic(planId, milestone.id);
  const updateTopic = useUpdateTopic(planId, milestone.id);
  const updateTopicStatus = useUpdateTopicStatus(planId, milestone.id);
  const deleteTopic = useDeleteTopic(planId, milestone.id);

  const sortedTopics = useMemo(
    () =>
      [...milestone.topics].sort(
        (a, b) => a.sortOrder - b.sortOrder || a.title.localeCompare(b.title),
      ),
    [milestone.topics],
  );

  const editingTopic = editingTopicId
    ? sortedTopics.find((topic) => topic.id === editingTopicId)
    : undefined;

  const handleTopicSubmit = (values: LearningTopicFormValues) => {
    const payload = toTopicRequest(values);
    if (editingTopic) {
      updateTopic.mutate(
        { topicId: editingTopic.id, payload },
        {
          onSuccess: () => {
            setTopicDialogOpen(false);
            setEditingTopicId(null);
          },
        },
      );
      return;
    }
    createTopic.mutate(payload, {
      onSuccess: () => setTopicDialogOpen(false),
    });
  };

  const handleStatusChange = (topicId: string, status: TopicStatus) => {
    updateTopicStatus.mutate({ topicId, status });
  };

  const topicFormRef = useRef<TopicFormHandle>(null);

  return (
    <Paper variant="outlined" sx={{ p: 2.5 }}>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} justifyContent="space-between">
        <Box sx={{ flex: 1 }}>
          <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
            <Typography variant="h6">{milestone.title}</Typography>
            {milestone.targetDate ? (
              <Typography variant="caption" color="text.secondary">
                Target: {formatDate(milestone.targetDate)}
              </Typography>
            ) : null}
          </Stack>
          {milestone.description ? (
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              {milestone.description}
            </Typography>
          ) : null}
          <Box sx={{ mt: 2 }}>
            <Stack
              direction="row"
              justifyContent="space-between"
              alignItems="center"
              sx={{ mb: 0.5 }}
            >
              <Typography variant="body2" color="text.secondary">
                Progress
              </Typography>
              <Typography variant="body2" fontWeight={600}>
                {milestone.progressPercent}%
              </Typography>
            </Stack>
            <LinearProgress
              variant="determinate"
              value={Math.max(0, Math.min(100, milestone.progressPercent))}
              sx={{ height: 6, borderRadius: 999 }}
            />
            <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5, display: 'block' }}>
              Topics completed: {milestone.completedTopics} / {milestone.totalTopics}
            </Typography>
          </Box>
        </Box>
        <Stack direction="row" spacing={1} alignSelf={{ xs: 'flex-start', sm: 'flex-start' }}>
          <IconButton aria-label="Edit milestone" onClick={() => onEditMilestone(milestone)}>
            <EditOutlinedIcon />
          </IconButton>
          <IconButton
            aria-label="Delete milestone"
            color="error"
            onClick={() => onDeleteMilestone(milestone)}
          >
            <DeleteOutlineIcon />
          </IconButton>
        </Stack>
      </Stack>

      <Box sx={{ mt: 3 }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
          <Typography variant="subtitle1" fontWeight={600}>
            Topics
          </Typography>
          <Button
            size="small"
            startIcon={<AddIcon />}
            onClick={() => {
              setEditingTopicId(null);
              setTopicDialogOpen(true);
            }}
          >
            Add topic
          </Button>
        </Stack>

        {sortedTopics.length === 0 ? (
          <EmptyState
            title="No topics yet"
            description="Add topics to track learning progress within this milestone."
            actionLabel="Add topic"
            onAction={() => {
              setEditingTopicId(null);
              setTopicDialogOpen(true);
            }}
          />
        ) : (
          <Stack spacing={1.5}>
            {sortedTopics.map((topic) => (
              <Paper key={topic.id} variant="outlined" sx={{ p: 2 }}>
                <Stack
                  direction={{ xs: 'column', md: 'row' }}
                  spacing={2}
                  alignItems={{ md: 'center' }}
                  justifyContent="space-between"
                >
                  <Box sx={{ flex: 1 }}>
                    <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap">
                      <Typography variant="subtitle2">{topic.title}</Typography>
                      <StatusChip status={topic.status} />
                    </Stack>
                    {topic.description ? (
                      <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                        {topic.description}
                      </Typography>
                    ) : null}
                  </Box>
                  <Stack direction="row" spacing={1} alignItems="center">
                    <TextField
                      select
                      size="small"
                      label="Status"
                      value={topic.status}
                      onChange={(event) =>
                        handleStatusChange(topic.id, event.target.value as TopicStatus)
                      }
                      disabled={updateTopicStatus.isPending}
                      sx={{ minWidth: 160 }}
                    >
                      {TOPIC_STATUSES.map((status) => (
                        <MenuItem key={status} value={status}>
                          {formatEnumLabel(status)}
                        </MenuItem>
                      ))}
                    </TextField>
                    <IconButton
                      aria-label="Edit topic"
                      onClick={() => {
                        setEditingTopicId(topic.id);
                        setTopicDialogOpen(true);
                      }}
                    >
                      <EditOutlinedIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      aria-label="Delete topic"
                      color="error"
                      onClick={() => setDeleteTopicId(topic.id)}
                    >
                      <DeleteOutlineIcon fontSize="small" />
                    </IconButton>
                  </Stack>
                </Stack>
              </Paper>
            ))}
          </Stack>
        )}
      </Box>

      <FormDialog
        open={topicDialogOpen}
        title={editingTopic ? 'Edit topic' : 'Add topic'}
        submitLabel={editingTopic ? 'Save changes' : 'Create topic'}
        loading={createTopic.isPending || updateTopic.isPending}
        onClose={() => {
          setTopicDialogOpen(false);
          setEditingTopicId(null);
        }}
        onSubmit={() => topicFormRef.current?.submit()}
      >
        <TopicForm
          ref={topicFormRef}
          defaultValues={editingTopic ? toTopicFormValues(editingTopic) : undefined}
          onSubmit={handleTopicSubmit}
        />
      </FormDialog>

      <ConfirmationDialog
        open={Boolean(deleteTopicId)}
        title="Delete topic"
        description="This topic will be permanently removed from the milestone."
        confirmLabel="Delete"
        danger
        loading={deleteTopic.isPending}
        onCancel={() => setDeleteTopicId(null)}
        onConfirm={() => {
          if (deleteTopicId) {
            deleteTopic.mutate(deleteTopicId, { onSuccess: () => setDeleteTopicId(null) });
          }
        }}
      />
    </Paper>
  );
}
