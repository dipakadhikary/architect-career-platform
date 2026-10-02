import { useMemo, useState } from 'react';
import { Button, IconButton, Stack, Tooltip, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import {
  ConfirmationDialog,
  DataTable,
  EmptyState,
  ErrorPanel,
  LoadingOverlay,
  type DataTableColumn,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { formatDate } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { AchievementFormDialog } from './AchievementFormDialog';
import { useAchievementMutations, useAchievementsQuery } from '../hooks/useAchievements';
import type { AchievementFormValues } from '../schemas/portfolio.schemas';
import { toAchievementRequest } from '../schemas/portfolio.schemas';
import type { Achievement } from '../types/portfolio.types';

export function AchievementsTab() {
  const notification = useNotification();
  const query = useAchievementsQuery();
  const { createMutation, updateMutation, deleteMutation } = useAchievementMutations();

  const [formOpen, setFormOpen] = useState(false);
  const [editingAchievement, setEditingAchievement] = useState<Achievement | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Achievement | null>(null);

  const rows = useMemo(() => query.data ?? [], [query.data]);

  const columns: DataTableColumn<Achievement>[] = [
    {
      id: 'title',
      label: 'Achievement',
      render: (row) => (
        <Typography variant="body2" fontWeight={600}>
          {row.title}
        </Typography>
      ),
    },
    {
      id: 'organization',
      label: 'Organization',
      width: 160,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {row.organization ?? '—'}
        </Typography>
      ),
    },
    {
      id: 'achievedOn',
      label: 'Date',
      width: 120,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {formatDate(row.achievedOn)}
        </Typography>
      ),
    },
    {
      id: 'description',
      label: 'Description',
      render: (row) => (
        <Typography
          variant="body2"
          color="text.secondary"
          noWrap
          sx={{ maxWidth: 360, display: 'block' }}
        >
          {row.description}
        </Typography>
      ),
    },
    {
      id: 'actions',
      label: '',
      width: 96,
      align: 'right',
      render: (row) => (
        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
          <Tooltip title="Edit">
            <IconButton
              size="small"
              aria-label="Edit achievement"
              onClick={() => {
                setEditingAchievement(row);
                setFormOpen(true);
              }}
            >
              <EditOutlinedIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              aria-label="Delete achievement"
              color="error"
              onClick={() => setDeleteTarget(row)}
            >
              <DeleteOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Stack>
      ),
    },
  ];

  const handleSubmit = async (values: AchievementFormValues) => {
    try {
      if (editingAchievement) {
        await updateMutation.mutateAsync({
          id: editingAchievement.id,
          payload: toAchievementRequest(values),
        });
        notification.success('Achievement updated');
      } else {
        await createMutation.mutateAsync(toAchievementRequest(values));
        notification.success('Achievement created');
      }
      setFormOpen(false);
      setEditingAchievement(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to save achievement'));
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteMutation.mutateAsync(deleteTarget.id);
      notification.success('Achievement deleted');
      setDeleteTarget(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to delete achievement'));
    }
  };

  const formLoading = createMutation.isPending || updateMutation.isPending;

  if (query.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(query.error, 'Unable to load achievements')}
        onRetry={() => void query.refetch()}
      />
    );
  }

  return (
    <>
      <Stack direction="row" justifyContent="flex-end" sx={{ mb: 2 }}>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => {
            setEditingAchievement(null);
            setFormOpen(true);
          }}
        >
          New achievement
        </Button>
      </Stack>

      {query.isLoading ? <LoadingOverlay open /> : null}

      {!query.isLoading && rows.length === 0 ? (
        <EmptyState
          title="No achievements yet"
          description="Highlight milestones and accomplishments from your career."
          actionLabel="New achievement"
          onAction={() => {
            setEditingAchievement(null);
            setFormOpen(true);
          }}
        />
      ) : (
        <DataTable
          columns={columns}
          rows={rows}
          rowKey={(row) => row.id}
          page={0}
          pageSize={rows.length || 10}
          totalElements={rows.length}
          onPageChange={() => undefined}
          onPageSizeChange={() => undefined}
        />
      )}

      <AchievementFormDialog
        open={formOpen}
        achievement={editingAchievement}
        loading={formLoading}
        onClose={() => {
          if (!formLoading) {
            setFormOpen(false);
            setEditingAchievement(null);
          }
        }}
        onSubmit={handleSubmit}
      />

      <ConfirmationDialog
        open={Boolean(deleteTarget)}
        title="Delete achievement"
        description={`Delete "${deleteTarget?.title}"? This action cannot be undone.`}
        confirmLabel="Delete"
        loading={deleteMutation.isPending}
        danger
        onCancel={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
      />
    </>
  );
}
