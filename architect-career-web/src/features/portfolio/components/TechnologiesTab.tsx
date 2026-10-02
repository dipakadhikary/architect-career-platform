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
import { TechnologyFormDialog } from './TechnologyFormDialog';
import { useTechnologiesQuery, useTechnologyMutations } from '../hooks/useTechnologies';
import type { TechnologyFormValues } from '../schemas/portfolio.schemas';
import { toTechnologyRequest } from '../schemas/portfolio.schemas';
import type { Technology } from '../types/portfolio.types';

export function TechnologiesTab() {
  const notification = useNotification();
  const query = useTechnologiesQuery();
  const { createMutation, updateMutation, deleteMutation } = useTechnologyMutations();

  const [formOpen, setFormOpen] = useState(false);
  const [editingTechnology, setEditingTechnology] = useState<Technology | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Technology | null>(null);

  const rows = useMemo(() => query.data ?? [], [query.data]);

  const columns: DataTableColumn<Technology>[] = [
    {
      id: 'name',
      label: 'Technology',
      render: (row) => (
        <Typography variant="body2" fontWeight={600}>
          {row.name}
        </Typography>
      ),
    },
    {
      id: 'category',
      label: 'Category',
      width: 180,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {row.category ?? '—'}
        </Typography>
      ),
    },
    {
      id: 'updatedAt',
      label: 'Updated',
      width: 120,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {formatDate(row.updatedAt)}
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
              aria-label="Edit technology"
              onClick={() => {
                setEditingTechnology(row);
                setFormOpen(true);
              }}
            >
              <EditOutlinedIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              aria-label="Delete technology"
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

  const handleSubmit = async (values: TechnologyFormValues) => {
    try {
      if (editingTechnology) {
        await updateMutation.mutateAsync({
          id: editingTechnology.id,
          payload: toTechnologyRequest(values),
        });
        notification.success('Technology updated');
      } else {
        await createMutation.mutateAsync(toTechnologyRequest(values));
        notification.success('Technology created');
      }
      setFormOpen(false);
      setEditingTechnology(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to save technology'));
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteMutation.mutateAsync(deleteTarget.id);
      notification.success('Technology deleted');
      setDeleteTarget(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to delete technology'));
    }
  };

  const formLoading = createMutation.isPending || updateMutation.isPending;

  if (query.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(query.error, 'Unable to load technologies')}
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
            setEditingTechnology(null);
            setFormOpen(true);
          }}
        >
          New technology
        </Button>
      </Stack>

      {query.isLoading ? <LoadingOverlay open /> : null}

      {!query.isLoading && rows.length === 0 ? (
        <EmptyState
          title="No technologies yet"
          description="Maintain a catalog of technologies used across your portfolio projects."
          actionLabel="New technology"
          onAction={() => {
            setEditingTechnology(null);
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

      <TechnologyFormDialog
        open={formOpen}
        technology={editingTechnology}
        loading={formLoading}
        onClose={() => {
          if (!formLoading) {
            setFormOpen(false);
            setEditingTechnology(null);
          }
        }}
        onSubmit={handleSubmit}
      />

      <ConfirmationDialog
        open={Boolean(deleteTarget)}
        title="Delete technology"
        description={`Delete "${deleteTarget?.name}"? This action cannot be undone.`}
        confirmLabel="Delete"
        loading={deleteMutation.isPending}
        danger
        onCancel={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
      />
    </>
  );
}
