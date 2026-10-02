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
  StatusChip,
  type DataTableColumn,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { formatDate } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { SkillFormDialog } from './SkillFormDialog';
import { useSkillMutations, useSkillsQuery } from '../hooks/useSkills';
import type { SkillFormValues } from '../schemas/portfolio.schemas';
import { toSkillRequest } from '../schemas/portfolio.schemas';
import type { Skill } from '../types/portfolio.types';

export function SkillsTab() {
  const notification = useNotification();
  const query = useSkillsQuery();
  const { createMutation, updateMutation, deleteMutation } = useSkillMutations();

  const [formOpen, setFormOpen] = useState(false);
  const [editingSkill, setEditingSkill] = useState<Skill | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Skill | null>(null);

  const rows = useMemo(() => query.data ?? [], [query.data]);

  const columns: DataTableColumn<Skill>[] = [
    {
      id: 'name',
      label: 'Skill',
      render: (row) => (
        <Typography variant="body2" fontWeight={600}>
          {row.name}
        </Typography>
      ),
    },
    {
      id: 'proficiencyLevel',
      label: 'Proficiency',
      width: 140,
      render: (row) => <StatusChip status={row.proficiencyLevel} />,
    },
    {
      id: 'yearsOfExperience',
      label: 'Experience',
      width: 120,
      render: (row) => (
        <Typography variant="body2" color="text.secondary">
          {row.yearsOfExperience != null ? `${row.yearsOfExperience} yrs` : '—'}
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
          {row.description ?? '—'}
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
              aria-label="Edit skill"
              onClick={() => {
                setEditingSkill(row);
                setFormOpen(true);
              }}
            >
              <EditOutlinedIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              aria-label="Delete skill"
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

  const handleSubmit = async (values: SkillFormValues) => {
    try {
      if (editingSkill) {
        await updateMutation.mutateAsync({ id: editingSkill.id, payload: toSkillRequest(values) });
        notification.success('Skill updated');
      } else {
        await createMutation.mutateAsync(toSkillRequest(values));
        notification.success('Skill created');
      }
      setFormOpen(false);
      setEditingSkill(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to save skill'));
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteMutation.mutateAsync(deleteTarget.id);
      notification.success('Skill deleted');
      setDeleteTarget(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Unable to delete skill'));
    }
  };

  const formLoading = createMutation.isPending || updateMutation.isPending;

  if (query.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(query.error, 'Unable to load skills')}
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
            setEditingSkill(null);
            setFormOpen(true);
          }}
        >
          New skill
        </Button>
      </Stack>

      {query.isLoading ? <LoadingOverlay open /> : null}

      {!query.isLoading && rows.length === 0 ? (
        <EmptyState
          title="No skills yet"
          description="Track your technical and professional skills with proficiency levels."
          actionLabel="New skill"
          onAction={() => {
            setEditingSkill(null);
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

      <SkillFormDialog
        open={formOpen}
        skill={editingSkill}
        loading={formLoading}
        onClose={() => {
          if (!formLoading) {
            setFormOpen(false);
            setEditingSkill(null);
          }
        }}
        onSubmit={handleSubmit}
      />

      <ConfirmationDialog
        open={Boolean(deleteTarget)}
        title="Delete skill"
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
