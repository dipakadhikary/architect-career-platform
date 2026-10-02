import { useState } from 'react';
import {
  Box,
  Button,
  IconButton,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import {
  ConfirmationDialog,
  EmptyState,
  ErrorPanel,
  LoadingSpinner,
  StatusChip,
} from '@/shared/components';
import { InterviewFormDialog } from '@/features/career/components/InterviewFormDialog';
import { mapInterviewFormToRequest } from '@/features/career/utils/formMappers';
import { useInterviewMutations, useInterviews } from '@/features/career/hooks/career.hooks';
import type { InterviewFormValues } from '@/features/career/schemas/career.schemas';
import type { InterviewResponse } from '@/features/career/types/career.types';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { formatDateTime } from '@/shared/utils/date';
import { formatEnumLabel } from '@/shared/utils/label';

interface InterviewsSectionProps {
  applicationId: string;
}

export function InterviewsSection({ applicationId }: InterviewsSectionProps) {
  const notification = useNotification();
  const { data, isLoading, isError, error, refetch } = useInterviews(applicationId);
  const { create, update, remove } = useInterviewMutations(applicationId);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<InterviewResponse | null>(null);
  const [deleting, setDeleting] = useState<InterviewResponse | null>(null);

  const handleSubmit = async (values: InterviewFormValues) => {
    try {
      const payload = mapInterviewFormToRequest(values);
      if (editing) {
        await update.mutateAsync({ interviewId: editing.id, payload });
        notification.success('Interview updated');
      } else {
        await create.mutateAsync(payload);
        notification.success('Interview scheduled');
      }
      setDialogOpen(false);
      setEditing(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to save interview'));
    }
  };

  const handleDelete = async () => {
    if (!deleting) return;
    try {
      await remove.mutateAsync(deleting.id);
      notification.success('Interview deleted');
      setDeleting(null);
    } catch (err) {
      notification.error(getErrorMessage(err, 'Failed to delete interview'));
    }
  };

  if (isLoading) {
    return <LoadingSpinner label="Loading interviews…" />;
  }

  if (isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(error, 'Failed to load interviews')}
        onRetry={() => void refetch()}
      />
    );
  }

  const interviews = data ?? [];

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h6">Interviews</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => {
            setEditing(null);
            setDialogOpen(true);
          }}
        >
          Schedule Interview
        </Button>
      </Stack>

      {interviews.length === 0 ? (
        <EmptyState
          title="No interviews yet"
          description="Schedule your first interview for this application."
          actionLabel="Schedule Interview"
          onAction={() => {
            setEditing(null);
            setDialogOpen(true);
          }}
        />
      ) : (
        <TableContainer component={Paper} variant="outlined">
          <Table size="medium">
            <TableHead>
              <TableRow>
                <TableCell>Round</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Interviewer</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Rating</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {interviews.map((interview) => (
                <TableRow key={interview.id} hover>
                  <TableCell>{formatEnumLabel(interview.interviewRound)}</TableCell>
                  <TableCell>{formatDateTime(interview.interviewDate)}</TableCell>
                  <TableCell>{interview.interviewer ?? '—'}</TableCell>
                  <TableCell>
                    <StatusChip status={interview.status} />
                  </TableCell>
                  <TableCell>{interview.rating ?? '—'}</TableCell>
                  <TableCell align="right">
                    <IconButton
                      size="small"
                      aria-label="Edit interview"
                      onClick={() => {
                        setEditing(interview);
                        setDialogOpen(true);
                      }}
                    >
                      <EditOutlinedIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      size="small"
                      aria-label="Delete interview"
                      onClick={() => setDeleting(interview)}
                    >
                      <DeleteOutlineIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <InterviewFormDialog
        open={dialogOpen}
        interview={editing}
        loading={create.isPending || update.isPending}
        onClose={() => {
          setDialogOpen(false);
          setEditing(null);
        }}
        onSubmit={(values) => void handleSubmit(values)}
      />

      <ConfirmationDialog
        open={Boolean(deleting)}
        title="Delete Interview"
        description="This will permanently remove the interview record."
        confirmLabel="Delete"
        danger
        loading={remove.isPending}
        onConfirm={() => void handleDelete()}
        onCancel={() => setDeleting(null)}
      />
    </Box>
  );
}
